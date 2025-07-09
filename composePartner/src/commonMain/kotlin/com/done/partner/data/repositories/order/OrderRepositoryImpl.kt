package com.done.partner.data.repositories.order

import com.done.core.data.util.ApiRoutes
import com.done.core.domain.repositories.event.TrackingRepository
import com.done.core.data.services.api.KtorApiService
import com.done.core.domain.services.notifications.NotificationService
import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.NetworkErrorName
import com.done.core.domain.util.result.Result
import com.done.partner.data.dto.order.OrderDto
import com.done.partner.data.dto.order.OrdersDataDto
import com.done.partner.data.dto.order.order_request.OptionItemDto
import com.done.partner.data.dto.order.order_request.OrderItemDto
import com.done.partner.data.dto.order.order_request.OrderRequestDto
import com.done.partner.data.dto.order.toOrder
import com.done.partner.data.dto.pagination.toPagination
import com.done.partner.domain.services.print.PrinterService
import com.done.partner.domain.models.cart.Cart
import com.done.partner.domain.models.orders.Order
import com.done.partner.domain.models.orders.status.MarkOrderAsStatus
import com.done.partner.domain.models.orders.status.OrderStatus
import com.done.partner.domain.repositories.order.OrderRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class OrderRepositoryImpl(
    private val apiService: KtorApiService,
    private val printerService: PrinterService,
    private val notificationService: NotificationService,
    private val trackingRepository: TrackingRepository
) : OrderRepository {

    override suspend fun getPrintLang(): String {
        return printerService.getPrintLang()
    }

    override suspend fun printOrder(
        ticket: ByteArray, printTwo: Boolean
    ) {
        printerService.printOrder(ticket, printTwo)
    }

    override suspend fun updateOrder(
        cart: Cart, orderId: String
    ): Result<Order, NetworkError> {
        val orderRequest = OrderRequestDto(
            orderItems = cart.cartProducts.map { cartProduct ->
                OrderItemDto(
                    productId = cartProduct.product.id,
                    quantity = cartProduct.quantity,
                    options = cartProduct.options.map { option ->
                        OptionItemDto(optionId = option.id, quantity = option.quantity)
                    })
            },
        )

        val result = apiService.put<OrderDto>(
            route = ApiRoutes.updateOrder(storeId = cart.store.id, orderId = orderId),
            body = orderRequest,
        )

        return when (result) {
            is Result.Error -> {
                Result.Error(result.error)
            }

            is Result.Success -> {
                result.data?.let { orderDto ->
                    return Result.Success(orderDto.toOrder())
                }
                return Result.Error(
                    NetworkError(NetworkErrorName.SERIALIZATION_ERROR)
                )
            }
        }
    }

    override suspend fun getOrders(
        storeId: String,
        status: List<String>?,
        page: Int
    ): Result<List<Order>, NetworkError> {

        val route = if (status == null) {
            ApiRoutes.getOrders(storeId = storeId, page = page)
        } else if (
            status.contains(OrderStatus.PENDING)
            && status.contains(OrderStatus.BEING_PREPARED)
            && status.contains(OrderStatus.READY_FOR_PICKUP)
        ) {
            ApiRoutes.acceptedAndPendingAndReadyToPickup(storeId = storeId, page = page)
        } else if (
            status.contains(OrderStatus.DELIVERED)
            && status.contains(OrderStatus.CANCELLED)
        ) {
            ApiRoutes.deliveredAndCancelled(storeId = storeId, page = page)
        } else {
            ApiRoutes.getOrders(storeId = storeId, page = page)
        }

        val result = apiService.get<OrdersDataDto>(route = route)

        if (result is Result.Error) {
            return Result.Error(result.error)
        }

        return try {
            val orders = result.data?.orderDtoList?.map { it.toOrder() } ?: emptyList()
            val pagination = result.data?.paginationMetaDto?.pagination?.toPagination()
            Result.Success(
                data = orders, pagination = pagination
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Result.Error(NetworkError(NetworkErrorName.UNKNOWN))
        }
    }

    override suspend fun getOrderDetails(
        storeId: String, orderId: String
    ): Result<Order, NetworkError> {

        val result = apiService.get<OrderDto>(
            route = ApiRoutes.getOrderDetails(storeId = storeId, orderId = orderId)
        )

        if (result is Result.Error) {
            return Result.Error(result.error)
        }

        return try {
            Result.Success(result.data?.toOrder())
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Result.Error(NetworkError(NetworkErrorName.UNKNOWN))
        }
    }

    override suspend fun updateOrderStatus(
        order: Order?,
        storeId: String,
        orderId: String,
        markOrderAsStatus: String,
        customerFriendlyCode: String,
    ): Result<Unit, NetworkError> {

        notificationService.cancelNotification(1)

        val result = apiService.post<Unit>(
            route = ApiRoutes.updateOrderStatus(
                storeId = storeId,
                orderId = orderId,
                markOrderAsStatus = markOrderAsStatus,
            ),
            body = if (markOrderAsStatus == MarkOrderAsStatus.DELIVERED) {
                mapOf("customer_friendly_code" to customerFriendlyCode)
            } else {
                emptyMap()
            }
        )

        order?.let {
            if (result is Result.Success) {
                if (markOrderAsStatus == MarkOrderAsStatus.BEING_PREPARED) {
                    trackAcceptOrderEvent(order)
                } else if (markOrderAsStatus == MarkOrderAsStatus.READY_FOR_PICKUP) {
                    trackOrderIsReadyEvent(order)
                }
            }
        }

        return handleResult(result)
    }


    private fun handleResult(
        result: Result<Unit, NetworkError>
    ): Result<Unit, NetworkError> {

        if (result is Result.Success) {
            return Result.Success(null)
        }

        return Result.Error(result.error)
    }

    private suspend fun trackAcceptOrderEvent(order: Order) {
        trackingRepository.trackAcceptOrderEvent(
            acceptanceTimestamp = Clock.System.now().toEpochMilliseconds().toString(),
            orderCreated = order.createdAt,
            orderId = order.id,
            customerId = order.customer?.id ?: "",
            orderItems = order.orderItems.size,
            orderValue = order.price?.display ?: "",
            estimatedPrepTime = order.preparationWillEndAt ?: "",
            specialInstructions = order.note
        )
    }

    private suspend fun trackOrderIsReadyEvent(order: Order) {
        trackingRepository.trackOrderIsReadyEvent(
            readyTimestamp = Clock.System.now().toEpochMilliseconds().toString(),
            originalEstimatedReadyTime = order.preparationWillEndAt ?: "",
            orderId = order.id,
            driverId = order.driver?.id ?: "",
            restaurantId = order.storeId
        )
    }
}