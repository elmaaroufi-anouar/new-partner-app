package com.done.partner.data.dto.driver

import com.done.partner.domain.models.driver.Driver


fun DriverDataDto.toDriver(): Driver {
    return Driver(
        availableAt = availableAt ?: "",
        city = city ?: "",
        email = email ?: "",
        firstName = firstName ?: "",
        averageRating = averageRating ?: 0.0,
        id = id ?: "",
        isAvailable = isAvailable == true,
        lastName = lastName ?: "",
        phone = phone ?: "",
        profileImageUrl = profileImageUrl ?: ""
    )
}