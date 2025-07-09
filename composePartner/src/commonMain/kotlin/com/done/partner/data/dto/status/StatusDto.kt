package com.done.partner.data.dto.status

import kotlinx.serialization.Serializable

@Serializable
data class StatusDto(
    val value: String? = null,
    val translation: String? = null
)
