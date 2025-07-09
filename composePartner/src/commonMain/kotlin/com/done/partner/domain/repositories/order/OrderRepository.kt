package com.done.partner.domain.repositories.order

import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.Result
import com.done.partner.domain.models.cart.Cart
import com.done.partner.domain.models.orders.Order

interface OrderRepository {

    suspend fun getPrintLang(): String

    suspend fun printOrder(
        ticket: ByteArray, printTwo: Boolean = false
    )

    suspend fun updateOrder(
        cart: Cart, orderId: String
    ): Result<Order, NetworkError>

    suspend fun getOrders(
        storeId: String,
        status: List<String>? = null,
        page: Int = 1
    ): Result<List<Order>, NetworkError>

    suspend fun getOrderDetails(
        storeId: String, orderId: String
    ): Result<Order, NetworkError>

    suspend fun updateOrderStatus(
        order: Order? = null,
        storeId: String,
        orderId: String,
        markOrderAsStatus: String,
        customerFriendlyCode: String,
    ): Result<Unit, NetworkError>
}