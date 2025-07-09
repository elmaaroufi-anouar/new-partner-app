package com.done.partner.data.dto.order.order_item_option

import com.done.partner.data.dto.price.PriceDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItemOptionDataOptionDto(
    @SerialName("data") val orderItemOptionDataOptionDtoDataDto: OrderItemOptionDataOptionDtoDataDto? = null
)

@Serializable
data class OrderItemOptionDataOptionDtoDataDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("additional_price") val additionalPrice: PriceDto? = null,
    @SerialName("choose_more_than_once") val chooseMoreThanOnce: Boolean? = null,
    @SerialName("option_group_id")val optionGroupId: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("is_enabled") val isEnabled: Boolean? = null
)