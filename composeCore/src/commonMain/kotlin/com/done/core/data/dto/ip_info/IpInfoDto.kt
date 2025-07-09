package com.done.core.data.dto.ip_info

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IpInfoDto(
    @SerialName("isp") val internetProvider: String? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("timezone") val timezone: String? = null,
    @SerialName("query") val ipAddress: String? = null
)