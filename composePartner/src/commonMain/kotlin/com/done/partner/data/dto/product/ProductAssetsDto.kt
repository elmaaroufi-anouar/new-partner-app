package com.done.partner.data.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductAssetsDto(
    @SerialName("data") val productAssetDtos: List<ProductAssetDto>? = null
)

@Serializable
data class ProductAssetDto(
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("file_path") val imageUrl: String? = null,
    @SerialName("file_type") val imageType: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("resource_type") val resourceType: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)