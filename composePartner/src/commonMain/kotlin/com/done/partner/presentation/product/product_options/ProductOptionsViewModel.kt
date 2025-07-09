package com.done.partner.presentation.product.product_options

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.partner.domain.repositories.prodcuts.ProductsRepository
import com.done.partner.domain.repositories.store.StoreRepository
import com.done.core.domain.util.result.Result
import com.done.core.presentation.core.util.toMap
import com.done.core.presentation.core.util.UiAction
import com.done.core.domain.repositories.analytics.AnalyticsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProductOptionsViewModel(
    private val storeRepository: StoreRepository,
    private val productsRepository: ProductsRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    var state by mutableStateOf(ProductOptionsState())
        private set

    private val eventChannel = Channel<OptionsEvent>()
    val event = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            snapshotFlow { state.priceTextState.text }.collectLatest { price ->
                val validPrice = price.filter { it.isDigit() }
                state = state.copy(
                    priceTextState = TextFieldState(validPrice.toString())
                )
            }
        }
    }

    fun onAction(action: ProductOptionsAction) {
        viewModelScope.launch {
            analyticsRepository.logEvent(UiAction.OptionsAction, action.toMap())
        }
        when (action) {
            is ProductOptionsAction.OnPullToRefresh -> {
                if (state.selectedStoreId != null) {
                    getProduct(action.productId)
                } else {
                    viewModelScope.launch {
                        state = state.copy(selectedStoreId = storeRepository.getStoreId())
                        getProduct(action.productId)
                    }
                }
            }

            is ProductOptionsAction.OnReload -> {
                if (state.selectedStoreId != null) {
                    getProduct(action.productId)
                } else {
                    viewModelScope.launch {
                        state = state.copy(selectedStoreId = storeRepository.getStoreId())
                        getProduct(action.productId)
                    }
                }
            }

            // ----

            is ProductOptionsAction.OnSelectOptionToDisable -> {
                selectOptionToDisable(action.optionId)
            }

            is ProductOptionsAction.OnUnselectOptionToDisable -> {
                unselectOptionToDisable(action.optionId)
            }

            ProductOptionsAction.OnAcceptDisableSelectedOptionsClick -> {
                acceptDisableSelectedOptions()
            }

            ProductOptionsAction.OnUndoDisableSelectedOptions -> {
                undoDisableSelectedOptions()
            }

            // ----

            is ProductOptionsAction.OnEnableOption -> {
                onEnableOption(
                    optionId = action.optionId
                )
            }

            // ----

            is ProductOptionsAction.OnToggleUpdateOptionPriceDialog -> {
                toggleUpdateOptionPriceDialog(action.optionId)
            }

            ProductOptionsAction.OnUpdateOptionPrice -> {
                updateOptionPrice()
            }

            is ProductOptionsAction.OnEnableOptionGroup -> {
                onEnableOptionGroup(action.optionGroupId)
            }

            ProductOptionsAction.OnBackClick -> {} // Handled by UI
        }
    }

    private fun updateOptionPrice() {
        viewModelScope.launch {
            state = state.copy(isUpdatingOptionPrice = true)

            val newPrice = state.priceTextState.text.toString().toIntOrNull()
            val selectedStoreId = state.selectedStoreId
            val productId = state.product?.id
            val selectedOptionToUpdatePriceId = state.selectedOptionToUpdatePriceId
            if (
                selectedStoreId != null
                && productId != null
                && selectedOptionToUpdatePriceId != null
                && newPrice != null
            ) {
                state = state.copy(
                    options = state.options.map { option ->
                        if (option.id == state.selectedOptionToUpdatePriceId) {
                            option.copy(
                                additionalPrice = option.additionalPrice?.copy(
                                    display = state.priceTextState.text.toString() + " " + option.additionalPrice?.currency,
                                    amount = newPrice.times(100.0)
                                )
                            )
                        } else {
                            option
                        }
                    }
                )
                val result = productsRepository.updateOptions(
                    storeId = selectedStoreId,
                    productId = productId,
                    options = state.options.filter { it.id == selectedOptionToUpdatePriceId }
                )
                state = state.copy(isUpdatingOptionPrice = false)

                when (result) {
                    is Result.Success -> {
                        state = state.copy(
                            isUpdateOptionPriceDialogShowing = false,
                            selectedOptionToUpdatePriceId = null,
                            priceTextState = TextFieldState()
                        )
                    }

                    is Result.Error -> {
                        eventChannel.send(OptionsEvent.Error(result.error))
                    }
                }
            } else {
                state = state.copy(isUpdatingOptionPrice = false)
            }
        }
    }

    private fun toggleUpdateOptionPriceDialog(optionId: String?) {
        // if productIndex is null, then we are dismissing the dialog,
        // which means selectedProductToUpdatePriceIndex will also be null.
        // if productIndex is not null, then we are showing the dialog.
        state = state.copy(
            isUpdateOptionPriceDialogShowing = !state.isUpdateOptionPriceDialogShowing,
            selectedOptionToUpdatePriceId = optionId,
            priceTextState = TextFieldState(
                if (optionId != null) "${
                    state.options.find { it.id == optionId }?.additionalPrice?.amount?.div(100)
                        ?.toInt()
                }" else ""
            )
        )
    }

    private fun onEnableOption(optionId: String) {
        viewModelScope.launch {
            state = state.copy(
                isUpdatingOptionsAvailability = true,
                selectedOptionToEnableId = optionId
            )

            val selectedStoreId = state.selectedStoreId
            val productId = state.product?.id

            if (
                selectedStoreId != null && productId != null
            ) {
                productsRepository.updateOptions(
                    storeId = selectedStoreId,
                    productId = productId,
                    options = state.options
                        .filter { it.id == optionId }
                        .map { it.copy(isEnabled = true) }
                )

                state = state.copy(
                    options = state.options.map { option ->
                        if (option.id == optionId) {
                            option.copy(isEnabled = true)
                        } else {
                            option
                        }
                    }
                )

                state = state.copy(
                    isUpdatingOptionsAvailability = false,
                    selectedOptionToEnableId = null
                )
            }
        }
    }

    private fun onEnableOptionGroup(optionGroupId: String) {
        viewModelScope.launch {
            state = state.copy(
                selectedOptionGroupToEnableId = optionGroupId
            )

            val selectedStoreId = state.selectedStoreId
            val productId = state.product?.id

            if (selectedStoreId != null && productId != null) {
                productsRepository.updateOptions(
                    storeId = selectedStoreId,
                    productId = productId,
                    options = state.options
                        .filter { it.optionGroupId == optionGroupId }
                        .map { it.copy(isEnabled = true) }
                )

                state = state.copy(
                    options = state.options.map { option ->
                        if (option.optionGroupId == optionGroupId) {
                            option.copy(isEnabled = true)
                        } else {
                            option
                        }
                    }
                )

                state = state.copy(
                    selectedOptionGroupToEnableId = null
                )
            }
        }
    }

    private fun acceptDisableSelectedOptions() {
        viewModelScope.launch {
            state = state.copy(isDisableOptionsFabShowing = false)

            state = state.copy(
                options = state.options.map { option ->
                    if (option.isSelectedToDisable) {
                        option.copy(isEnabled = false)
                    } else {
                        option
                    }
                }
            )

            val selectedStoreId = state.selectedStoreId
            val productId = state.product?.id

            if (selectedStoreId != null && productId != null) {
                productsRepository.updateOptions(
                    storeId = selectedStoreId,
                    productId = productId,
                    options = state.options.filter { it.isSelectedToDisable }
                )
            }

            state = state.copy(
                options = state.options.map { product ->
                    if (product.isSelectedToDisable) {
                        product.copy(isEnabled = false, isSelectedToDisable = false)
                    } else {
                        product
                    }
                },
            )
        }
    }

    private fun undoDisableSelectedOptions() {
        viewModelScope.launch {
            state = state.copy(
                isUpdatingOptionsAvailability = true
            )

            state = state.copy(
                options = state.options.map { product ->
                    if (product.isSelectedToDisable) {
                        product.copy(isEnabled = true, isSelectedToDisable = false)
                    } else {
                        product
                    }
                },
                isUpdatingOptionsAvailability = false,
            )
        }
    }

    private fun unselectOptionToDisable(optionId: String) {
        state = state.copy(
            options = state.options.mapIndexed { index, option ->
                if (option.id == optionId) {
                    option.copy(isSelectedToDisable = false)
                } else {
                    option
                }
            }
        )
        if (state.options.none { it.isSelectedToDisable }) {
            state = state.copy(
                isDisableOptionsFabShowing = false
            )
        }
    }

    private fun selectOptionToDisable(productId: String) {
        state = state.copy(
            options = state.options.map { product ->
                if (product.id == productId) {
                    product.copy(isSelectedToDisable = true)
                } else {
                    product
                }
            },
        )
        if (!state.isDisableOptionsFabShowing) {
            state = state.copy(
                isDisableOptionsFabShowing = true
            )
        }
    }

    private fun getProduct(productId: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            state.selectedStoreId?.let { storeId ->
                val result = productsRepository.getProduct(
                    storeId = storeId,
                    productId = productId
                )
                state = state.copy(isLoading = false)

                if (result is Result.Success) {
                    state = state.copy(
                        product = result.data,
                        options = result.data?.optionGroups?.flatMap { it.options } ?: emptyList()
                    )
                } else {
                    eventChannel.send(OptionsEvent.Error(result.error))
                }
            }
        }
    }
}
