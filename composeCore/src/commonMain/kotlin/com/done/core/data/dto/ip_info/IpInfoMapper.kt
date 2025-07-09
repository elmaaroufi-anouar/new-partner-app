package com.done.core.data.dto.ip_info

import com.done.core.domain.models.ip_info.IpInfo

fun IpInfoDto.toIpInfo() = IpInfo(
    internetProvider = internetProvider ?: "",
    country = country ?: "",
    timezone = timezone ?: "",
    ipAddress = ipAddress ?: ""
)