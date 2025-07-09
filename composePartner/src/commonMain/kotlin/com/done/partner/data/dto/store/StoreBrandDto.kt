package com.done.partner.data.dto.store

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreBrandDto(
    @SerialName("data") val storeBrandDataDto: StoreBrandDataDto? = null
)

@Serializable
data class StoreBrandDataDto(
    @SerialName("resource_type") val resourceType: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)