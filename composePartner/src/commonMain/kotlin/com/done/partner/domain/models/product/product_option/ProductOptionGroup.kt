package com.done.partner.domain.models.product.product_option

data class ProductOptionGroup(
    val id: String,
    val productId: String,
    val name: String,
    val isRequired: Boolean,
    val minOptions: Int,
    val maxOptions: Int,
    val isEnabled: Boolean,
    val options: List<ProductOption>
)