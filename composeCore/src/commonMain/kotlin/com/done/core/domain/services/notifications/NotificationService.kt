package com.done.core.domain.services.notifications

interface NotificationService {
    fun cancelNotification(id: Int)

    fun subscribeToGlobalTopic()

    fun subscribeToUserTopic(userId: String)

    fun unsubscribeFromTopics(userId: String)
}