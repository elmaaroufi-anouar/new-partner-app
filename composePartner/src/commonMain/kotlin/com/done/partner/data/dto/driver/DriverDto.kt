package com.done.partner.data.dto.driver

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DriverDto(
    @SerialName("data") val driverDataDto: DriverDataDto? = null
)

@Serializable
data class DriverDataDto(
    @SerialName("available_at") val availableAt: String? = null,
    @SerialName("city") val city: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("is_available") val isAvailable: Boolean? = null,
    @SerialName("average_rating") val averageRating: Double? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("profile_file_path") val profileImageUrl: String? = null
)