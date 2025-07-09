package com.done.partner.data.dto.delivery

import com.done.partner.data.dto.price.toPrice
import com.done.partner.domain.models.delivery.Delivery

fun DeliveryDataDto.toDelivery(): Delivery {
    return Delivery(
        id = id ?: "",
        acceptedAt = acceptedAt ?: "",
        arrivedAtPickupAddressAt = arrivedAtPickupAddressAt ?: "",
        cancellationReason = cancellationReason ?: "",
        cancelledAt = cancelledAt ?: "",
        createdAt = createdAt ?: "",
        deliveredAt = deliveredAt ?: "",
        deliveryAddress = deliveryAddress ?: "",
        deliveryLatitude = deliveryLatitude ?: 0.0,
        deliveryLongitude = deliveryLongitude ?: 0.0,
        deliveryPrice = deliveryPrice?.toPrice(),
        departureLatitude = departureLatitude ?: 0.0,
        departureLongitude = departureLongitude ?: 0.0,
        departureToPickupDistance = departureToPickupDistance ?: 0,
        departureToPickupEstimatedTime = departureToPickupEstimatedTime ?: 0,
        pickupLatitude = pickupLatitude ?: 0.0,
        pickupLongitude = pickupLongitude ?: 0.0,
        pickupToDeliveryDistance = pickupToDeliveryDistance ?: 0,
        pickupToDeliveryEstimatedTime = pickupToDeliveryEstimatedTime ?: 0,
        pickedUpAt = pickedUpAt ?: "",
        receiptPath = receiptPath ?: "",
        resourceType = resourceType ?: "",
        status = status ?: ""
    )
}