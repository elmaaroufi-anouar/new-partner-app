package com.done.partner.domain.models.delivery

import com.done.partner.domain.models.price.Price

data class Delivery(
    val id: String,
    val acceptedAt: String,
    val arrivedAtPickupAddressAt: String,
    val cancellationReason: String,
    val cancelledAt: String,
    val createdAt: String,
    val deliveredAt: String,
    val deliveryAddress: String,
    val deliveryLatitude: Double,
    val deliveryLongitude: Double,
    val deliveryPrice: Price?,
    val departureLatitude: Double,
    val departureLongitude: Double,
    val departureToPickupDistance: Int,
    val departureToPickupEstimatedTime: Int,
    val pickupLatitude: Double,
    val pickupLongitude: Double,
    val pickupToDeliveryDistance: Int,
    val pickupToDeliveryEstimatedTime: Int,
    val pickedUpAt: String,
    val receiptPath: String,
    val resourceType: String,
    val status: String
)
