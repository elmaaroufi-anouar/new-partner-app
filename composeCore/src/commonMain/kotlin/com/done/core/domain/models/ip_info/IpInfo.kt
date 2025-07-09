package com.done.core.domain.models.ip_info

data class IpInfo(
    val internetProvider: String,
    val country: String,
    val timezone: String,
    val ipAddress: String
)