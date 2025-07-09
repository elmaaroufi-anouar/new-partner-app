package com.done.partner.data.dto.store

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreDto(
    @SerialName("resource_type") val resourceType: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("business_email") val businessEmail: String? = null,
    @SerialName("invoice_email") val invoiceEmail: String? = null,
    @SerialName("sections") val sections: StoreSectionDto? = null,
    @SerialName("disabled_at") val disabledAt: String? = null,
    @SerialName("ice_file_path") val iceFilePath: String? = null,
    @SerialName("rib_file_path") val ribFilePath: String? = null,
    @SerialName("longitude") val longitude: String? = null,
    @SerialName("latitude") val latitude: String? = null,
    @SerialName("brand") val brand: StoreBrandDto? = null
)