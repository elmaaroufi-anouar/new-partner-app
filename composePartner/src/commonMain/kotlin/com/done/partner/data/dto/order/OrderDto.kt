package com.done.partner.data.dto.order

import com.done.partner.data.dto.customer.CustomerDto
import com.done.partner.data.dto.delivery.DeliveryDto
import com.done.partner.data.dto.driver.DriverDto
import com.done.partner.data.dto.order.order_item.OrderItemsDto
import com.done.partner.data.dto.pagination.PaginationMetaDto
import com.done.partner.data.dto.price.PriceDto
import com.done.partner.data.dto.status.StatusDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrdersDataDto(
    @SerialName("data") val orderDtoList: List<OrderDto>? = null,
    @SerialName("meta") val paginationMetaDto: PaginationMetaDto? = null
)

@Serializable
data class OrderDto(
    @SerialName("id") val id: String? = null,
    @SerialName("customer_id") val customerId: String? = null,
    @SerialName("store_id") val storeId: String? = null,
    @SerialName("delivery_id") val deliveryId: String? = null,
    @SerialName("in_store_pickup") val inStorePickup: Boolean? = null,
    @SerialName("note") val note: String? = null,
    @SerialName("total_price") val price: PriceDto? = null,
    @SerialName("products_amount") val productsAmount: PriceDto? = null,
    @SerialName("friendly_code") val friendlyNumber: String? = null,
    @SerialName("delivery_estimation_in_min") val deliveryEstimationInMin: Double? = null,
    @SerialName("delivery_estimation_in_km") val deliveryEstimationInKm: Double? = null,
    @SerialName("status") val status: StatusDto? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("deleted_at") val deletedAt: String? = null,
    @SerialName("status_updated_at") val statusUpdatedAt: String? = null,
    @SerialName("order_items") val orderItems: OrderItemsDto? = null,
    @SerialName("customer") val customer: CustomerDto? = null,
    @SerialName("expected_to_be_ready_at") val preparationWillEndAt: String? = null,
    @SerialName("driver") val driver: DriverDto? = null,
    @SerialName("delivery") val delivery: DeliveryDto? = null
)