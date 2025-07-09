package com.done.core.data.services.notifications

import com.done.core.domain.services.notifications.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.printStackTrace
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import me.pushy.sdk.Pushy

actual class NotificationServiceImpl(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat
) : NotificationService {
    private val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
    private val globalTopic = "partner"

    actual override fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    actual override fun subscribeToGlobalTopic() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Pushy.subscribe(globalTopic, context)
                println("subscribeToGlobalTopic globalTopic : $globalTopic")
            } catch (e: Exception) {
                println("can't subscribeToGlobalTopic globalTopic : $globalTopic ${e.printStackTrace()}")
                e.printStackTrace()
            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic(globalTopic)
        markSubscribed(globalTopic)
    }

    actual override fun subscribeToUserTopic(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Pushy.subscribe(userId, context)
                println("subscribeToUserTopic userId : $userId")
            } catch (e: Exception) {
                println("can't subscribeToUserTopic userId : $userId ${e.printStackTrace()}")
                e.printStackTrace()
            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic(userId)
        markSubscribed(userId)
    }

    actual override fun unsubscribeFromTopics(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Pushy.unsubscribe(userId, context)
                println("unsubscribe userId : $userId")
            } catch (e: Exception) {
                println("can't unsubscribe userId : $userId")
                e.printStackTrace()
            }
        }

        FirebaseMessaging.getInstance().unsubscribeFromTopic(globalTopic)
        FirebaseMessaging.getInstance().unsubscribeFromTopic(userId)

        clearSubscription(globalTopic)
        clearSubscription(userId)
    }

    private fun markSubscribed(topic: String) {
        prefs.edit { putBoolean(topic, true) }
    }

    private fun clearSubscription(topic: String) {
        prefs.edit { remove(topic) }
    }
}