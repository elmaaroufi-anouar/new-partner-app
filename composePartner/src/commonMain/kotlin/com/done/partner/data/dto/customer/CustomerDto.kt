package com.done.partner.data.dto.customer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerDto(
    @SerialName("data") val customerDataDto: CustomerDataDto? = null
)

@Serializable
data class CustomerDataDto(
    @SerialName("email") val email: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("phone") val phone: String? = null
)