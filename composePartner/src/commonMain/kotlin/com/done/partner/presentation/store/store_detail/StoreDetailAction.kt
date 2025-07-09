package com.done.partner.presentation.store.store_detail

import com.done.partner.domain.models.cart.CartProduct
import com.done.partner.domain.models.cart.CartProductOption
import com.done.partner.domain.models.product.Product
import com.done.core.presentation.core.util.Action

sealed interface StoreDetailAction : Action {
    data class OnLoad(val orderId: String) : StoreDetailAction

    data class OnPullToRefresh(val orderId: String) : StoreDetailAction

    data class OnOpenProductSheet(
        val product: Product,
        val cartProduct: CartProduct?
    ) : StoreDetailAction

    data object OnCloseProductSheet : StoreDetailAction
    data object OnConfirmOrder : StoreDetailAction
    data object OnBackClick : StoreDetailAction

    data class OnIncrementQuantity(
        val product: Product,
        val productOptions: List<CartProductOption>,
        val quantity: Int = 1,
    ) : StoreDetailAction

    data class OnDecrementQuantity(
        val product: Product,
        val productOptions: List<CartProductOption>,
        val removeAll: Boolean = false,
    ) : StoreDetailAction
}