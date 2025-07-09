package com.done.partner.data.dto.order.order_item

import com.done.partner.data.dto.product.ProductDataDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItemProductDto(
    @SerialName("data") val productDataDto: ProductDataDto? = null
)