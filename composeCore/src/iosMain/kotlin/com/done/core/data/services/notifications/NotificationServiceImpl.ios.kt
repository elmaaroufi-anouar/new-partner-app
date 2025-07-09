package com.done.core.data.services.notifications

import com.done.core.domain.services.notifications.NotificationService

actual class NotificationServiceImpl : NotificationService {
    actual override fun cancelNotification(id: Int) {
    }

    actual override fun subscribeToGlobalTopic() {
    }

    actual override fun subscribeToUserTopic(userId: String) {
    }

    actual override fun unsubscribeFromTopics(userId: String) {
    }
}