package com.done.core.data.services.notifications

import com.done.core.domain.services.notifications.NotificationService

expect class NotificationServiceImpl : NotificationService {
    override fun cancelNotification(id: Int)
    override fun subscribeToGlobalTopic()
    override fun subscribeToUserTopic(userId: String)
    override fun unsubscribeFromTopics(userId: String)
}
