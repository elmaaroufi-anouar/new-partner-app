package com.done.app.util

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.media.AudioManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.done.core.data.util.jsonWithUnknownKeys
import com.done.partner.data.dto.status.StatusDto
import com.done.core.domain.services.auth_response.AuthResponseService
import com.done.core.domain.services.secure_storage.SecureStorageService
import com.done.core.domain.models.notification.OrderNotification
import com.done.app.App
import com.done.app.MainActivity
import com.done.app.PartnerApplication
import com.done.partner.R
import com.done.partner.domain.repositories.settings.SettingsRepository
import com.done.core.domain.services.notifications.DeviceTokenRegisteringService
import com.done.partner.domain.DEEPLINK_BASE_PATH
import com.done.partner.domain.util.OrderNotificationsSender
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.android.ext.android.inject

class FirebaseService : FirebaseMessagingService() {

    private val secureStorageService: SecureStorageService by inject()
    private val notificationManager: NotificationManagerCompat by inject()
    private val authResponseService: AuthResponseService by inject()
    private val deviceTokenRegisteringService: DeviceTokenRegisteringService by inject()
    private val orderNotificationsSender: OrderNotificationsSender by inject()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.notification?.let { notification ->
            val dataMap = remoteMessage.data
            val data = dataMap["entity_data"]
            val type = dataMap["entity_type"]

            try {
                val token = runBlocking { authResponseService.getAuthToken() }

                if (token != null) {
                    val orderNotification = if (type == "order" && data != null) {
                        val orderData = jsonWithUnknownKeys.decodeFromString<JsonObject>(data)
                        val orderId = orderData["id"]?.jsonPrimitive?.content
                        val orderStatus = orderData["status"]?.toString()?.let {
                            jsonWithUnknownKeys.decodeFromString<StatusDto>(it)
                        }

                        OrderNotification(
                            orderId = orderId, status = orderStatus?.value ?: ""
                        )
                    } else {
                        null
                    }

                    if (type == "order") {
                        sendNotification(orderNotification)
                    }

                    val showNotification = runBlocking {
                        secureStorageService.getBoolean(SettingsRepository.KEY_RECEIVE_NOTIFICATIONS, true)
                    }
                    if (showNotification) {
                        showNotification(
                            notification = notification,
                            orderNotification = orderNotification
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) throw e
            }
        }
    }

    private fun sendNotification(orderNotification: OrderNotification?) {
        orderNotification?.let {
            scope.launch {
                orderNotificationsSender.sendNotification(orderNotification)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(
        notification: RemoteMessage.Notification,
        orderNotification: OrderNotification?
    ) {

        setMaxNotificationVolume()

        val activityIntent = Intent(this, MainActivity::class.java).apply {
            if (orderNotification != null) {
                val orderId = orderNotification.orderId
                val orderStatus = orderNotification.status
                data = "${DEEPLINK_BASE_PATH}/$orderId/$orderStatus".toUri()
            }
        }
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(activityIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        val sound = "${ContentResolver.SCHEME_ANDROID_RESOURCE}://$packageName/raw/$${notification.sound}".toUri()

        val messageBody = notification.body
        val messageTitle = notification.title
        val builder = NotificationCompat
            .Builder(this, notification.channelId ?: PartnerApplication.Companion.DONE_NOTIFICATION_BACKUP_CHANNEL)
            .setSmallIcon(R.drawable.logo_letter_primary_color_notif)
            .setContentTitle(messageBody)
            .setContentText(messageTitle)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(sound)
            .build()

        notificationManager.notify(1, builder)
    }

    private fun setMaxNotificationVolume() {
        try {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, maxVolume, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewToken(token: String) {
        scope.launch {
            deviceTokenRegisteringService.registerDeviceToken(token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}