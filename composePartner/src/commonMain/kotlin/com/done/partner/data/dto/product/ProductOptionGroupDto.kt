package com.done.partner.data.dto.product

import com.done.partner.data.dto.price.PriceDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductOptionGroupDto(
    @SerialName("data") val data: List<ProductOptionGroupDataDto>? = null,
)

@Serializable
data class ProductOptionGroupDataDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("is_required") val isRequired: Boolean? = null,
    @SerialName("min_options") val minOptions: Int? = null,
    @SerialName("max_options") val maxOptions: Int? = null,
    @SerialName("options") val options: ProductOptionDto? = null,
    @SerialName("is_enabled") val isEnabled: Boolean? = null
)

@Serializable
data class ProductOptionDto(
    @SerialName("data") val data: List<ProductOptionDataDto>? = null,
)

@Serializable
data class ProductOptionDataDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("additional_price") val additionalPrice: PriceDto? = null,
    @SerialName("choose_more_than_once") val chooseMoreThanOnce: Boolean? = null,
    @SerialName("option_group_id") val optionGroupId: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("is_enabled") val isEnabled: Boolean? = null
)