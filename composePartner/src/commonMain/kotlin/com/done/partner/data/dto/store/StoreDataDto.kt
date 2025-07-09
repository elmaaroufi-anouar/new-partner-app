package com.done.partner.data.dto.store

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreDataDto(
    @SerialName("data") val storeDtos: List<StoreDto>? = null
)
