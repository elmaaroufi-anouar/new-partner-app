package com.done.partner.data.dto.order.order_item

import com.done.partner.data.dto.order.order_item_option.OrderItemOptionsDto
import com.done.partner.data.dto.price.PriceDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItemsDto(
    @SerialName("data") val data: List<OrderItemDataDto>? = null
)

@Serializable
data class OrderItemDataDto(
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("deleted_at") val deletedAt: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("order_id") val orderId: String? = null,
    @SerialName("order_item_options") val orderItemOptions: OrderItemOptionsDto? = null,
    @SerialName("total_price") val price: PriceDto? = null,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("quantity") val quantity: Int? = null,
    @SerialName("resource_type") val resourceType: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("product") val product: OrderItemProductDto? = null
)