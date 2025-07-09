package com.done.partner.presentation.product.products

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
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val storeRepository: StoreRepository,
    private val productsRepository: ProductsRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    var state by mutableStateOf(ProductsState())
        private set

    private val eventChannel = Channel<ProductsEvent>()
    val event = eventChannel.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            snapshotFlow { state.priceTextState.text }.collectLatest { price ->
                val validPrice = price.filter { it.isDigit() }
                state = state.copy(
                    priceTextState = TextFieldState(validPrice.toString())
                )
            }
        }

        viewModelScope.launch {
            snapshotFlow { state.searchProductsQueryState.text }.collectLatest { q ->
                if (q.isNotEmpty()) {
                    searchProducts()
                }
            }
        }

        viewModelScope.launch {
            state = state.copy(selectedStoreId = storeRepository.getStoreId())
        }
    }

    fun onAction(action: ProductsAction) {
        viewModelScope.launch {
            analyticsRepository.logEvent(UiAction.ProductsAction, action.toMap())
        }
        when (action) {
            ProductsAction.OnToggleStoreAvailabilityDialog -> {
                state = state.copy(
                    isToggleStoreAvailabilityDialogShowing = !state.isToggleStoreAvailabilityDialogShowing
                )
            }

            ProductsAction.OnToggleStoreAvailability -> {
                changeStoreAvailability()
            }

            ProductsAction.OnPaginate -> {
                val pagination = state.pagination

                if (pagination != null && pagination.totalPages > pagination.currentPage) {

                    state = state.copy(
                        pagination = pagination.copy(currentPage = pagination.currentPage + 1)
                    )

                    getProducts(paginate = true)
                }
            }

            ProductsAction.OnPullToRefresh -> {
                getStores(getProductsToo = state.selectedStoreId == null)
                if (state.selectedStoreId != null) {
                    getProducts()
                }
            }

            ProductsAction.OnReload -> {
                getStores(getProductsToo = state.selectedStoreId == null)
                if (state.selectedStoreId != null) {
                    getProducts()
                }
            }

            // ----

            is ProductsAction.OnSelectProductToDisable -> {
                selectProductToDisable(action.productIndex)
            }

            is ProductsAction.OnUnselectProductToDisable -> {
                unselectProductToDisable(action.productIndex)
            }

            ProductsAction.OnAcceptDisableSelectedProductsClick -> {
                state.selectedStoreId?.let { storeId ->
                    acceptDisableSelectedProducts(storeId = storeId)
                }
            }

            ProductsAction.OnUndoDisableSelectedProducts -> {
                undoDisableSelectedProducts()
            }

            // ----

            is ProductsAction.OnEnableProduct -> {
                state.selectedStoreId?.let { storeId ->
                    onEnableProduct(
                        storeId = storeId,
                        productIndex = action.productIndex
                    )
                }
            }

            // ----

            is ProductsAction.OnToggleUpdateProductPriceDialog -> {
                toggleUpdateProductPriceDialog(action.productIndex)
            }

            ProductsAction.OnUpdateProductPrice -> {
                updateProductPrice()
            }

            is ProductsAction.OnProductClick -> {} // Handled by UI
        }
    }

    private fun updateProductPrice() {
        viewModelScope.launch {
            state = state.copy(isUpdatingProductPrice = true)

            val newPrice = state.priceTextState.text.toString().toIntOrNull()
            val selectedStoreId = state.selectedStoreId
            val selectedProductToUpdatePriceIndex = state.selectedProductToUpdatePriceIndex
            if (selectedStoreId != null && selectedProductToUpdatePriceIndex != null && newPrice != null) {
                val result = productsRepository.updateProductPrice(
                    storeId = selectedStoreId,
                    productId = state.products[selectedProductToUpdatePriceIndex].id,
                    price = newPrice
                )

                state = state.copy(isUpdatingProductPrice = false)
                when (result) {
                    is Result.Success -> {
                        state = state.copy(
                            products = state.products.mapIndexed { index, product ->
                                if (index == selectedProductToUpdatePriceIndex) {
                                    product.copy(
                                        price = product.price?.copy(
                                            display = state.priceTextState.text.toString() + " " + product.price?.currency,
                                            amount = newPrice.times(100.0)
                                        )
                                    )
                                } else {
                                    product
                                }
                            }
                        )

                        state = state.copy(
                            isUpdateProductPriceDialogShowing = false,
                            selectedProductToUpdatePriceIndex = null,
                            priceTextState = TextFieldState()
                        )
                    }

                    is Result.Error -> {
                        eventChannel.send(ProductsEvent.Error(result.error))
                    }
                }
            } else {
                state = state.copy(isUpdatingProductPrice = false)
            }
        }
    }

    private fun toggleUpdateProductPriceDialog(productIndex: Int?) {
        // if productIndex is null, then we are dismissing the dialog,
        // which means selectedProductToUpdatePriceIndex will also be null.
        // if productIndex is not null, then we are showing the dialog.

        state = state.copy(
            isUpdateProductPriceDialogShowing = !state.isUpdateProductPriceDialogShowing,
            selectedProductToUpdatePriceIndex = productIndex,
            priceTextState = TextFieldState(
                if (productIndex != null) "${state.products[productIndex].price?.amount?.div(100)?.toInt()}"
                else ""
            )
        )
    }

    private fun onEnableProduct(storeId: String, productIndex: Int) {
        viewModelScope.launch {
            hideUndoSnackbar()
            state = state.copy(
                isUpdatingProductsAvailability = true,
                selectedProductToEnableIndex = productIndex
            )

            state.products[productIndex].id.let { productId ->
                productsRepository.enableProduct(
                    storeId = storeId,
                    productId = productId
                )

                state = state.copy(
                    products = state.products.mapIndexed { index, product ->
                        if (index == productIndex) {
                            product.copy(isEnabled = true)
                        } else {
                            product
                        }
                    }
                )
            }

            state = state.copy(
                isUpdatingProductsAvailability = false,
                selectedProductToEnableIndex = null
            )

        }

    }

    private fun acceptDisableSelectedProducts(storeId: String) {
        viewModelScope.launch {

            state = state.copy(
                isDisableProductsFabShowing = false,
                isUndoDisableProductsSnackbarShowing = true
            )

            state = state.copy(
                products = state.products.map { product ->
                    if (product.isSelectedToDisable) {
                        product.copy(isEnabled = false)
                    } else {
                        product
                    }
                }
            )

            delay(3000)

            hideUndoSnackbar(unselectProduct = false)

            state.products.forEach { product ->
                if (product.isSelectedToDisable) {
                    productsRepository.disableProduct(
                        storeId = storeId,
                        productId = product.id
                    )
                }
            }
            state = state.copy(
                products = state.products.map { product ->
                    if (product.isSelectedToDisable) {
                        product.copy(isEnabled = false, isSelectedToDisable = false)
                    } else {
                        product
                    }
                },
            )
        }
    }

    private fun hideUndoSnackbar(unselectProduct: Boolean = true) {
        if (state.isUndoDisableProductsSnackbarShowing) {
            state = state.copy(
                isUndoDisableProductsSnackbarShowing = false
            )

            if (unselectProduct) {
                state = state.copy(
                    products = state.products.map { it.copy(isSelectedToDisable = false) }
                )
            }
        }
    }

    private fun undoDisableSelectedProducts() {
        viewModelScope.launch {
            state = state.copy(
                isUndoDisableProductsSnackbarShowing = false,
                isUpdatingProductsAvailability = true
            )

            state = state.copy(
                products = state.products.map { product ->
                    if (product.isSelectedToDisable) {
                        product.copy(isEnabled = true, isSelectedToDisable = false)
                    } else {
                        product
                    }
                },
                isUpdatingProductsAvailability = false,
            )
        }
    }

    private fun unselectProductToDisable(productIndex: Int) {
        state = state.copy(
            products = state.products.mapIndexed { index, product ->
                if (index == productIndex) {
                    product.copy(isSelectedToDisable = false)
                } else {
                    product
                }
            }
        )
        if (state.products.none { it.isSelectedToDisable }) {
            state = state.copy(
                isDisableProductsFabShowing = false
            )
        }
    }

    private fun selectProductToDisable(productIndex: Int) {
        hideUndoSnackbar()
        state = state.copy(
            products = state.products.mapIndexed { index, product ->
                if (index == productIndex) {
                    product.copy(isSelectedToDisable = true)
                } else {
                    product
                }
            },
        )
        if (!state.isDisableProductsFabShowing) {
            state = state.copy(
                isDisableProductsFabShowing = true
            )
        }
    }

    private fun getProducts(paginate: Boolean = false) {
        viewModelScope.launch {
            if (!paginate) {
                state = state.copy(
                    isLoadingProducts = true,
                    pagination = state.pagination?.copy(currentPage = 1)
                )
            }

            state.selectedStoreId?.let { storeId ->
                val result = productsRepository.getProducts(
                    storeId = storeId,
                    page = state.pagination?.currentPage ?: 1,
                    query = state.searchProductsQueryState.text.toString()
                )
                state = state.copy(isLoadingProducts = false)

                if (result is Result.Success) {
                    val list = result.data ?: emptyList()
                    state = state.copy(
                        products = if (paginate) state.products + list else list,
                        pagination = result.pagination
                    )
                } else {
                    eventChannel.send(ProductsEvent.Error(result.error))
                }
            }
        }
    }

    private fun searchProducts() {
        searchJob?.cancel()
        searchJob = null
        searchJob = viewModelScope.launch {
            delay(500)
            state = state.copy(
                isLoadingProducts = true,
                pagination = state.pagination?.copy(currentPage = 1)
            )

            state.selectedStoreId?.let { storeId ->
                val result = productsRepository.getProducts(
                    storeId = storeId,
                    page = 1,
                    query = state.searchProductsQueryState.text.toString()
                )

                if (result is Result.Success) {
                    state = state.copy(
                        products = result.data ?: emptyList(),
                        pagination = result.pagination
                    )
                } else {
                    eventChannel.send(ProductsEvent.Error(result.error))
                }
            }

            state = state.copy(isLoadingProducts = false)
        }
    }

    private fun getStores(getProductsToo: Boolean = false) {
        viewModelScope.launch {

            state = state.copy(
                isLoadingStore = true,
                isLoadingProducts = true
            )

            val result = storeRepository.getStores()
            if (result is Result.Success) {
                state = state.copy(
                    stores = result.data ?: emptyList(),
                    selectedStore = result.data?.firstOrNull(),
                    isStoreOpen = result.data?.firstOrNull()?.disabledAt == null
                )

                if (getProductsToo) {
                    getProducts()
                }

            } else {
                state = state.copy(isLoadingProducts = false)
                eventChannel.send(ProductsEvent.Error(result.error))
            }

            state = state.copy(isLoadingStore = false)
        }
    }

    private fun changeStoreAvailability() {
        state = state.copy(isToggleStoreAvailabilityDialogShowing = false)

        state.selectedStore?.id?.let { storeId ->

            viewModelScope.launch {
                state = state.copy(isChangingStoreAvailability = true)

                val result = if (state.isStoreOpen) {
                    storeRepository.closeStore(storeId)
                } else {
                    storeRepository.openStore(storeId)
                }

                if (result is Result.Success) {
                    state = state.copy(
                        isStoreOpen = !state.isStoreOpen
                    )
                } else {
                    eventChannel.send(ProductsEvent.Error(result.error))
                }

                state = state.copy(isChangingStoreAvailability = false)
            }
        }
    }
}
