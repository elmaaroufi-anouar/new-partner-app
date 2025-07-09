package com.done.partner.presentation.store.product_detail

import com.done.partner.domain.models.cart.CartProduct
import com.done.partner.domain.models.cart.CartProductOption
import com.done.partner.domain.models.product.Product

data class ProductDetailState (
    val optionGroupStates: Map<String, OptionGroupState> = emptyMap(),
    val productOptions: List<CartProductOption> = emptyList(),
    val product: Product? = null,
    val cartProduct: CartProduct? = null,
    val quantity: Int = 1,
    val canAddProduct: Boolean = false,
)