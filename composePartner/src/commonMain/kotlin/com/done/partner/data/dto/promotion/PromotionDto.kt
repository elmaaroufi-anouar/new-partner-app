package com.done.partner.data.dto.promotion

import com.done.partner.data.dto.price.PriceDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromotionDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("promotion_type") val promotionType: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("value_string") val valueString: String? = null,
    @SerialName("value_object") val valuePrice: PriceDto? = null,
    @SerialName("min_order_total") val minOrderTotal: PriceDto? = null,
)

@Serializable
data class PromotionsDto(
    @SerialName("data") val data: List<PromotionDto>? = null,
)