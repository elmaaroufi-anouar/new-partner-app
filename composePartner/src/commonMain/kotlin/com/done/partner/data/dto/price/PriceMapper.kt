package com.done.partner.data.dto.price

import com.done.partner.domain.models.price.Price

fun PriceDto.toPrice(): Price {
    return Price(
        amount = amount ?: 0.0,
        currency = currency ?: "",
        display = display ?: ""
    )
}