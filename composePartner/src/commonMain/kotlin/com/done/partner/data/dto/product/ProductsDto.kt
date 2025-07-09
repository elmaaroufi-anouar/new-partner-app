package com.done.partner.data.dto.product

import com.done.partner.data.dto.pagination.PaginationMetaDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsDto(
    @SerialName("data") val products: List<ProductDataDto>? = null,
    @SerialName("meta") val paginationMetaDto: PaginationMetaDto? = null
)
