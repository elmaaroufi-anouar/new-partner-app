package com.done.partner.domain.models.orders

import com.done.partner.domain.models.price.Price
import com.done.partner.domain.models.product.Product

data class OrderItem(
    val createdAt: String,
    val deletedAt: String,
    val name: String,
    val id: String,
    val orderId: String,
    val orderItemOptions: List<OrderItemOption>,
    val price: Price?,
    val currency: String,
    val productId: String,
    val quantity: Int,
    val updatedAt: String,
    val product: Product?
)
