package com.done.partner.domain.models.cart

import com.done.partner.domain.models.product.Product

data class CartProduct(
    val product: Product,
    val options: List<CartProductOption>,
    val quantity: Int,
)

