package com.done.partner.presentation.order_details

import com.done.core.domain.util.result.NetworkError

sealed interface OrderDetailsEvent {
    data object OrderStatusUpdated: OrderDetailsEvent
    data class Error(val networkError: NetworkError?): OrderDetailsEvent
}