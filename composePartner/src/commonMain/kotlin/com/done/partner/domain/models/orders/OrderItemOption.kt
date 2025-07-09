package com.done.partner.domain.models.orders

import com.done.partner.domain.models.price.Price
import com.done.partner.domain.models.product.product_option.ProductOption

data class OrderItemOption(
    val createdAt: String,
    val deletedAt: String,
    val id: String,
    val name: String,
    val optionId: String,
    val orderItemId: String,
    val optionGroupId: String,
    val price: Price?,
    val quantity: Int,
    val updatedAt: String,
    val option: ProductOption?
)
