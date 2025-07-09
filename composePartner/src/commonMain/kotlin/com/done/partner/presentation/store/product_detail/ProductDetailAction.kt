package com.done.partner.presentation.store.product_detail

import com.done.partner.domain.models.cart.CartProduct
import com.done.partner.domain.models.cart.CartProductOption
import com.done.partner.domain.models.product.Product
import com.done.core.presentation.core.util.Action

sealed interface ProductDetailAction : Action {
    data object OnBack : ProductDetailAction

    data class OnFirstLoad(
        val product: Product,
        val cartProduct: CartProduct?
    ) : ProductDetailAction

    data class OnOptionGroupStateChange(
        val optionGroupState: OptionGroupState,
        val index: Int
    ) : ProductDetailAction

    data object OnIncrement : ProductDetailAction
    data object OnDecrement : ProductDetailAction

    data class OnIncrementQuantity(
        val product: Product,
        val productOptions: List<CartProductOption>,
        val quantity: Int = 1
    ) : ProductDetailAction

    data class OnDecrementQuantity(
        val product: Product,
        val productOptions: List<CartProductOption>,
        val removeAll: Boolean = false
    ) : ProductDetailAction

    data object OnHighlightRequiredFields : ProductDetailAction
}