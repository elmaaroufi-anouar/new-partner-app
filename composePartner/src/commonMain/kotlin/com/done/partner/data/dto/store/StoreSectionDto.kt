package com.done.partner.data.dto.store

import com.done.partner.data.dto.product.ProductsDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreSectionDto(
    val data: List<StoreSectionDataDto>? = null,
)

@Serializable
data class StoreSectionDataDto(
    @SerialName("resource_type") val resourceType: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("sort_order") val sortOrder: Int? = null,
    @SerialName("storeId") val storeId: String? = null,
    @SerialName("products") val products: ProductsDto? = null,
    @SerialName("layout") val layout: String? = null
)