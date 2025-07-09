package com.done.partner.data.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductIngredientDto(
    val data: List<ProductIngredientDataDto>? = null,
)

@Serializable
data class ProductIngredientDataDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
)