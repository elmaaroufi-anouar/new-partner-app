package com.done.partner.presentation.order_list

import com.done.core.domain.util.result.NetworkError

sealed interface OrderListEvent {
    data object RestartApp: OrderListEvent
    data object Unauthorized: OrderListEvent
    data class Error(val networkError: NetworkError?): OrderListEvent
}