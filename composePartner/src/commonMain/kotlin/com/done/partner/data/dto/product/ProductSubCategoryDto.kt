package com.done.partner.data.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductSubCategoryDto(
    @SerialName("data") val data: List<ProductSubCategoryDataDto>? = null,
)

@Serializable
data class ProductSubCategoryDataDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("category") val category: ProductCategoryDto? = null,
)

@Serializable
data class ProductCategoryDto(
    val data: ProductCategoryDataDto? = null,
)

@Serializable
data class ProductCategoryDataDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
)