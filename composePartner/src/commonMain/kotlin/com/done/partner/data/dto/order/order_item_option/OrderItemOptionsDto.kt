package com.done.partner.data.dto.order.order_item_option

import com.done.partner.data.dto.price.PriceDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItemOptionsDto(
    @SerialName("data") val data: List<OrderItemOptionDataDto>? = null
)

@Serializable
data class OrderItemOptionDataDto(
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("deleted_at") val deletedAt: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("option_id") val optionId: String? = null,
    @SerialName("order_item_id") val orderItemId: String? = null,
    @SerialName("option_group_id") val optionGroupId: String? = null,
    @SerialName("total_price") val price: PriceDto? = null,
    @SerialName("quantity") val quantity: Int? = null,
    @SerialName("resource_type") val resourceType: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("option") val option: OrderItemOptionDataOptionDto? = null
)