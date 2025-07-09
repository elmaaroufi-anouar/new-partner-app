package com.done.partner.presentation.store.product_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.partner.domain.models.cart.CartProduct
import com.done.partner.domain.models.cart.CartProductOption
import com.done.partner.domain.models.product.Product
import com.done.core.presentation.core.util.toMap
import com.done.core.presentation.core.util.UiAction
import com.done.core.domain.repositories.analytics.AnalyticsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<ProductDetailEvent>()
    val event = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            _state.map { it.productOptions }
                .distinctUntilChanged()
                .collectLatest { productOptions ->
                    checkCanAddProduct(productOptions)
                }

        }
    }

    fun onAction(action: ProductDetailAction) {
        viewModelScope.launch {
            analyticsRepository.logEvent(UiAction.ProductDetailAction, action.toMap())
        }
        when (action) {
            is ProductDetailAction.OnFirstLoad -> onFirstLoad(
                action.product,
                action.cartProduct
            )

            is ProductDetailAction.OnOptionGroupStateChange -> onSelectionChange(
                action.optionGroupState,
                action.index
            )

            ProductDetailAction.OnDecrement -> onDecrement()

            ProductDetailAction.OnIncrement -> onIncrement()

            ProductDetailAction.OnBack -> {
                _state.value = _state.value.copy(
                    optionGroupStates = emptyMap(),
                    product = null,
                    cartProduct = null,
                    quantity = 1,
                    productOptions = emptyList()
                )
            }

            is ProductDetailAction.OnDecrementQuantity -> {
                // Handled bu UI
            }

            is ProductDetailAction.OnIncrementQuantity -> {
                // Handled bu UI
            }

            ProductDetailAction.OnHighlightRequiredFields -> {
                viewModelScope.launch {
                    eventChannel.send(
                        ProductDetailEvent.HighlightRequiredFields
                    )
                }
            }
        }
    }

    private fun checkCanAddProduct(productOptions: List<CartProductOption>) {
        val selectedOptionsByGroup = productOptions
            .groupBy { it.groupId }
            .mapValues { it.value.size }

        val allRequiredGroupsSatisfied = _state.value.product?.optionGroups
            ?.filter { it.isRequired }
            ?.all { group ->
                val selectedCount = selectedOptionsByGroup[group.id] ?: 0
                selectedCount == group.minOptions
            } == true

        _state.value = _state.value.copy(canAddProduct = allRequiredGroupsSatisfied)
    }

    private fun onFirstLoad(product: Product, cartProduct: CartProduct?) {
        val optionGroupStates = product.optionGroups.associate { group ->
            group.id to when {
                group.maxOptions == 1 -> OptionGroupState.Radio(
                    selectedOption = cartProduct?.options?.find { it.groupId == group.id }?.id
                )

                group.options.isNotEmpty() && group.options[0].chooseMoreThanOnce -> OptionGroupState.Button(
                    quantities = group.options.associate { option ->
                        val quantity = cartProduct?.options?.find {
                            it.groupId == group.id && it.id == option.id
                        }?.quantity ?: 0
                        option.id to quantity
                    }.toMutableMap()
                )

                else -> OptionGroupState.Checkbox(
                    checkedItems = group.options.associate { option ->
                        val isChecked = cartProduct?.options?.any {
                            it.groupId == group.id && it.id == option.id
                        } == true
                        option.id to isChecked
                    }.toMutableMap()
                )
            }
        }.toMutableMap()

        _state.value = _state.value.copy(
            optionGroupStates = optionGroupStates,
            product = product,
            cartProduct = cartProduct,
            canAddProduct = product.optionGroups.isEmpty(),
            quantity = cartProduct?.quantity ?: 1,
            productOptions = cartProduct?.options ?: emptyList()
        )

        checkCanAddProduct(_state.value.productOptions)
    }

    private fun onSelectionChange(optionGroupState: OptionGroupState, index: Int) {
        val group = _state.value.product?.optionGroups?.get(index)
        val optionGroupStates = _state.value.optionGroupStates.toMutableMap().apply {
            if (group != null) {
                this[group.id] = optionGroupState
            }
        }

        _state.value = _state.value.copy(optionGroupStates = optionGroupStates)
        _state.value = _state.value.copy(productOptions = getAllSelectedOptions())
    }

    private fun getAllSelectedOptions(): List<CartProductOption> {
        return _state.value.optionGroupStates.flatMap { (groupId, groupState) ->
            when (groupState) {
                is OptionGroupState.Radio -> {
                    groupState.selectedOption?.let { selectedOptionId ->
                        listOf(
                            CartProductOption(
                                id = selectedOptionId,
                                groupId = groupId,
                                name = "",
                                quantity = 1
                            )
                        )
                    } ?: emptyList()
                }

                is OptionGroupState.Button -> {
                    groupState.quantities.filterValues { it > 0 }.map { (optionId, quantity) ->
                        CartProductOption(
                            id = optionId,
                            groupId = groupId,
                            name = "",
                            quantity = quantity
                        )
                    }
                }

                is OptionGroupState.Checkbox -> {
                    groupState.checkedItems.filterValues { it }.map { (optionId, _) ->
                        CartProductOption(id = optionId, groupId = groupId, name = "", quantity = 1)
                    }
                }
            }
        }
    }

    private fun onIncrement() {
        _state.value = _state.value.copy(quantity = _state.value.quantity + 1)
    }

    private fun onDecrement() {
        if (_state.value.quantity > 1) {
            _state.value = _state.value.copy(quantity = _state.value.quantity - 1)
        }
    }
}