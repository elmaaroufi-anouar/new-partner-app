package com.done.partner.data.dto.delivery

import com.done.partner.data.dto.price.PriceDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDto(
    @SerialName("data") val deliveryDataDto: DeliveryDataDto? = null
)

@Serializable
data class DeliveryDataDto(
    @SerialName("id") val id: String? = null,
    @SerialName("accepted_at") val acceptedAt: String? = null,
    @SerialName("arrived_to_pickup_address_at") val arrivedAtPickupAddressAt: String? = null,
    @SerialName("cancellation_reason") val cancellationReason: String? = null,
    @SerialName("cancelled_at") val cancelledAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("delivered_at") val deliveredAt: String? = null,
    @SerialName("delivery_address") val deliveryAddress: String? = null,
    @SerialName("delivery_latitude") val deliveryLatitude: Double? = null,
    @SerialName("delivery_longitude") val deliveryLongitude: Double? = null,
    @SerialName("delivery_price") val deliveryPrice: PriceDto? = null,
    @SerialName("departure_latitude") val departureLatitude: Double? = null,
    @SerialName("departure_longitude") val departureLongitude: Double? = null,
    @SerialName("departure_to_pickup_distance") val departureToPickupDistance: Int? = null,
    @SerialName("departure_to_pickup_estimated_time") val departureToPickupEstimatedTime: Int? = null,
    @SerialName("pickup_latitude") val pickupLatitude: Double? = null,
    @SerialName("pickup_longitude") val pickupLongitude: Double? = null,
    @SerialName("pickup_to_delivery_distance") val pickupToDeliveryDistance: Int? = null,
    @SerialName("pickup_to_delivery_estimated_time") val pickupToDeliveryEstimatedTime: Int? = null,
    @SerialName("pickupped_at") val pickedUpAt: String? = null,
    @SerialName("receipt_path") val receiptPath: String? = null,
    @SerialName("resource_type") val resourceType: String? = null,
    @SerialName("status") val status: String? = null
)