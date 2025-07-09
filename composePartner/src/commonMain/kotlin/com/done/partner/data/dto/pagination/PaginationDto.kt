package com.done.partner.data.dto.pagination

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationMetaDto(
    @SerialName("pagination") val pagination: PaginationDto? = null
)

@Serializable
data class PaginationDto(
    @SerialName("total") val total: Int? = null,
    @SerialName("count") val count: Int? = null,
    @SerialName("per_page") val perPage: Int? = null,
    @SerialName("current_page") val currentPage: Int? = null,
    @SerialName("total_pages") val totalPages: Int? = null
)