package com.done.partner.presentation.store.store_detail

import com.done.partner.domain.models.cart.Cart
import com.done.partner.domain.models.cart.CartProduct
import com.done.partner.domain.models.orders.Order
import com.done.partner.domain.models.product.Product
import com.done.partner.domain.models.store.Store

data class StoreDetailState (
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isUpdatingOrder: Boolean = false,
    val store: Store? = null,
    val order: Order? = null,
    val isProductSheetShown: Boolean = false,
    val selectedProduct: Product? = null,
    val selectedCartProduct: CartProduct? = null,
    val cart: Cart? = null,
    val totalPrice: Double = 0.0,
    val countProducts: Int = 0,
)