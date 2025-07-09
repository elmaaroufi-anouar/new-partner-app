package com.done.partner.data.dto.order.order_request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestDto(
    @SerialName("order_items") val orderItems: List<OrderItemDto>? = null
)

@Serializable
data class OrderItemDto(
    @SerialName("product_id") val productId: String? = null,
    val quantity: Int? = null,
    val options: List<OptionItemDto>? = null
)

@Serializable
data class OptionItemDto(
    @SerialName("option_id") val optionId: String? = null,
    val quantity: Int? = null,
)