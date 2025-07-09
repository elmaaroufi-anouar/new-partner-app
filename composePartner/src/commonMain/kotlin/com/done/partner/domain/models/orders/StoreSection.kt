package com.done.partner.domain.models.orders

import com.done.partner.domain.models.product.Product

data class StoreSection(
    val id: String,
    val name: String,
    val sortOrder: Int,
    val storeId: String,
    val layout: String,
    val products: List<Product> = emptyList(),
)
