package com.done.app.util

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.done.app.App
import com.done.app.MainActivity
import com.done.app.PartnerApplication
import com.done.core.data.util.ApiRoutes
import com.done.core.data.util.jsonWithUnknownKeys
import com.done.partner.data.dto.status.StatusDto
import com.done.core.data.services.api.KtorApiService
import com.done.core.domain.models.notification.OrderNotification
import com.done.partner.R
import com.done.partner.domain.DEEPLINK_BASE_PATH
import com.done.partner.domain.util.OrderNotificationsSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.pushy.sdk.Pushy
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PushReceiver : BroadcastReceiver(), KoinComponent {

    private val apiService: KtorApiService by inject()
    private val notificationManager: NotificationManagerCompat by inject()
    private val orderNotificationsSender: OrderNotificationsSender by inject()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        val entityType = intent.getStringExtra("entity_type")
        val entityData = intent.getStringExtra("entity_data")

        var notificationData = OrderNotification(orderId = "", status = "")

        if (entityData != null) {
            val orderData = jsonWithUnknownKeys.decodeFromString<JsonObject>(entityData)

            if (entityType == "order") {
                val orderId = orderData["id"]?.jsonPrimitive?.content
                val orderStatus = orderData["status"]?.toString()?.let {
                    jsonWithUnknownKeys.decodeFromString<StatusDto>(it)
                }
                notificationData = OrderNotification(
                    orderId = orderId, status = orderStatus?.value ?: ""
                )
                sendNotification(notificationData)

                orderId?.let {
                    scope.launch {
                        apiService.post<Unit>(route = ApiRoutes.orderTracking(orderId, "new-order-notification-received"))
                    }
                }

            } else if (entityType == "store") {
                val storeId = orderData["store_id"]?.jsonPrimitive?.content
                val action = orderData["action"]?.jsonPrimitive?.content
                if (action == "ping_pos" && storeId != null) {
                    scope.launch {
                        apiService.post<Unit>(route = ApiRoutes.posHeartbeat(storeId = storeId))
                    }
                    return
                }
            }
        }

        val notificationTitle = if (intent.getStringExtra("title") != null) {
            intent.getStringExtra("title")
        } else {
            context.packageManager.getApplicationLabel(context.applicationInfo).toString()
        }

        val notificationBody = if (intent.getStringExtra("body") != null) {
            intent.getStringExtra("body")
        } else {
            ""
        }

        val notificationChannel = if (intent.getStringExtra("channel") != null) {
            intent.getStringExtra("channel")
        } else {
            ""
        }

        val notificationSound = if (intent.getStringExtra("sound") != null) {
            intent.getStringExtra("sound")
        } else {
            ""
        }
        val sound =
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/raw/$${notificationSound}".toUri()

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            val linkOrderId = notificationData.orderId
            val linkOrderStatus = notificationData.status
            data = "${DEEPLINK_BASE_PATH}/$linkOrderId/$linkOrderStatus".toUri()
        }
        val pendingIntent = if (notificationData.orderId?.isNotEmpty() == true) {
            TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(activityIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }
        } else {
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val builder = NotificationCompat
            .Builder(context, notificationChannel ?: PartnerApplication.DONE_NOTIFICATION_BACKUP_CHANNEL)
            .setSmallIcon(R.drawable.logo_letter_primary_color_notif)
            .setContentTitle(notificationTitle)
            .setContentText(notificationBody)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(sound)
            .build()

        try {
            Pushy.setNotificationChannel(builder, context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(1, builder)
    }

    private fun sendNotification(orderNotification: OrderNotification?) {
        orderNotification?.let {
            scope.launch {
                orderNotificationsSender.sendNotification(orderNotification)
            }
        }
    }
}