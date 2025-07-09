package com.done.partner.domain.util

import com.done.core.domain.models.notification.OrderNotification
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class OrderNotificationsSender {
    private val _notificationsFlow = MutableSharedFlow<OrderNotification>()
    val notificationsFlow = _notificationsFlow.asSharedFlow()

    suspend fun sendNotification(orderNotification: OrderNotification) {
        _notificationsFlow.emit(orderNotification)
    }
}
