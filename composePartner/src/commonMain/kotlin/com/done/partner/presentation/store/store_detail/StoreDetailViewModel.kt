package com.done.partner.presentation.store.store_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.partner.data.dto.order.toCartProduct
import com.done.partner.domain.models.cart.Cart
import com.done.partner.domain.models.cart.CartProduct
import com.done.partner.domain.models.cart.CartProductOption
import com.done.partner.domain.models.product.Product
import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.NetworkErrorName
import com.done.core.domain.util.result.Result
import com.done.partner.domain.models.store.Store
import com.done.core.presentation.core.util.toMap
import com.done.partner.domain.repositories.order.OrderRepository
import com.done.partner.domain.repositories.store.StoreRepository
import com.done.core.presentation.core.util.UiAction
import com.done.core.domain.repositories.analytics.AnalyticsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StoreDetailViewModel(
    private val storeRepository: StoreRepository,
    private val orderRepository: OrderRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StoreDetailState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<StoreDetailEvent>()
    val event = eventChannel.receiveAsFlow()

    fun onAction(action: StoreDetailAction) {
        viewModelScope.launch {
            analyticsRepository.logEvent(UiAction.StoreDetailAction, action.toMap())
        }
        when (action) {
            is StoreDetailAction.OnLoad -> {
                loadOrderAndStore(orderId = action.orderId)
            }

            is StoreDetailAction.OnPullToRefresh -> {
                loadOrderAndStore(refresh = true, orderId = action.orderId)
            }

            is StoreDetailAction.OnOpenProductSheet -> {
                openProductSheet(
                    action.product, action.cartProduct
                )
            }

            StoreDetailAction.OnCloseProductSheet -> closeProductSheet()

            is StoreDetailAction.OnIncrementQuantity -> {
                incrementProduct(
                    action.product, action.productOptions, action.quantity
                )
            }

            is StoreDetailAction.OnDecrementQuantity -> {
                decrementProduct(
                    action.product, action.productOptions, action.removeAll,
                )
            }

            StoreDetailAction.OnConfirmOrder -> {
                confirmOrder()
            }

            StoreDetailAction.OnBackClick -> {} // Handled bu UI
        }
    }

    private fun confirmOrder() {
        viewModelScope.launch {
            _state.update { it.copy(isUpdatingOrder = true) }

            val cart = _state.value.cart
            val orderId = _state.value.order?.id
            if (cart != null && orderId != null) {
                val result = orderRepository.updateOrder(
                    cart = cart, orderId = orderId
                )

                _state.update { it.copy(isUpdatingOrder = false) }

                when (result) {
                    is Result.Error -> {
                        eventChannel.send(StoreDetailEvent.Error(result.error))
                    }

                    is Result.Success -> {
                        eventChannel.send(StoreDetailEvent.OrderUpdated)
                    }
                }
            }

            _state.update { it.copy(isUpdatingOrder = false) }
        }
    }

    private fun loadOrderAndStore(refresh: Boolean = false, orderId: String) {
        viewModelScope.launch {
            val storeId = storeRepository.getStoreId()
            if (storeId != null) {
                _state.value = if (refresh) {
                    _state.value.copy(isRefreshing = true)
                } else {
                    _state.value.copy(isLoading = true)
                }

                val result = orderRepository.getOrderDetails(storeId = storeId, orderId = orderId)
                if (result is Result.Success) {
                    result.data?.let { order ->
                        _state.update {
                            it.copy(order = order)
                        }
                        loadStore()
                    }
                } else {
                    _state.update {
                        it.copy(isLoading = false, isRefreshing = false)
                    }
                    eventChannel.send(StoreDetailEvent.Error(result.error))
                }
            } else {
                _state.update {
                    it.copy(isLoading = false, isRefreshing = false)
                }
                eventChannel.send(StoreDetailEvent.Error(NetworkError(NetworkErrorName.UNKNOWN)))
            }
        }
    }

    private fun loadStore() {
        viewModelScope.launch {
            storeRepository.getStoreId()?.let { storeId ->
                val result = storeRepository.getStore(storeId)
                _state.update {
                    it.copy(isLoading = false, isRefreshing = false)
                }
                if (result is Result.Success) {
                    result.data?.let { store ->
                        _state.update {
                            it.copy(store = store)
                        }
                        getCart(store)
                    }
                } else {
                    eventChannel.send(StoreDetailEvent.Error(result.error))
                }
            } ?: eventChannel.send(StoreDetailEvent.Error(NetworkError(NetworkErrorName.UNKNOWN)))
        }
    }

    private fun closeProductSheet() {
        _state.update {
            it.copy(
                isProductSheetShown = false,
                selectedProduct = null,
                selectedCartProduct = null
            )
        }
    }

    private fun openProductSheet(product: Product, cartProduct: CartProduct?) {
        _state.update {
            it.copy(
                selectedProduct = product,
                isProductSheetShown = true,
                selectedCartProduct = cartProduct
            )
        }
    }

    private fun incrementProduct(
        product: Product,
        options: List<CartProductOption>,
        quantity: Int,
    ) {
        _state.value.cart?.let { cart ->
            if (_state.value.store?.id == cart.store.id) {
                val cartProduct = cart.cartProducts.find {
                    it.product.id == product.id && it.options == options
                }

                val updatedProducts = if (cartProduct != null) {
                    cart.cartProducts.map { existingProduct ->
                        if (existingProduct.product.id == product.id && existingProduct.options == options) {
                            existingProduct.copy(quantity = existingProduct.quantity + quantity)
                        } else {
                            existingProduct
                        }
                    }
                } else {
                    cart.cartProducts + CartProduct(
                        product = product,
                        options = options,
                        quantity = quantity
                    )
                }

                val updatedCart = cart.copy(cartProducts = updatedProducts)
                _state.update {
                    it.copy(cart = updatedCart)
                }

                calculateCart()
            } else if (_state.value.store != null) {
                val newCartProduct =
                    CartProduct(product = product, options = options, quantity = quantity)
                val newCart =
                    cart.copy(store = _state.value.store!!, cartProducts = listOf(newCartProduct))
                _state.update {
                    it.copy(cart = newCart)
                }

                calculateCart()
            }
        }
    }

    private fun decrementProduct(
        product: Product,
        options: List<CartProductOption>,
        removeAll: Boolean
    ) {
        _state.value.cart?.let { cart ->
            if (_state.value.store?.id == cart.store.id) {
                val updatedProducts = cart.cartProducts.mapNotNull { existingProduct ->
                    if (existingProduct.product.id == product.id && existingProduct.options == options) {
                        if (removeAll) {
                            null
                        } else if (existingProduct.quantity > 1) {
                            existingProduct.copy(quantity = existingProduct.quantity - 1)
                        } else {
                            null
                        }
                    } else {
                        existingProduct
                    }
                }

                val updatedCart = cart.copy(cartProducts = updatedProducts)
                _state.update {
                    it.copy(cart = updatedCart)
                }

                calculateCart()
            }
        }
    }

    private fun calculateCart() {
        _state.update {
            it.copy(
                countProducts = it.cart?.cartProducts?.sumOf { it.quantity } ?: 0
            )
        }
    }

    private fun getCart(store: Store) {
        viewModelScope.launch {
            val orderItems = _state.value.order?.orderItems
            val cart = if (orderItems?.isNotEmpty() == true) {
                Cart(
                    store = store,
                    cartProducts = orderItems.mapNotNull { it.toCartProduct() },
                )
            } else {
                Cart(
                    store = store,
                    cartProducts = emptyList()
                )
            }

            _state.update {
                it.copy(cart = cart)
            }

            calculateCart()

            viewModelScope.launch {
                val firstSelectedProductId = cart.cartProducts.firstOrNull()?.product?.id

                if (firstSelectedProductId != null) {
                    store.storeSections.forEachIndexed { sectionIndex, section ->
                        val productIndex =
                            section.products.indexOfFirst { it.id == firstSelectedProductId }
                        if (productIndex != -1) {
                            eventChannel.send(
                                StoreDetailEvent.ScrollToFirstSelectedProduct(
                                    sectionIndex, productIndex
                                )
                            )
                            return@forEachIndexed
                        }
                    }
                }
            }
        }
    }
}