package com.done.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.done.app.di.initKoin
import com.done.core.data.services.language.LanguageServiceImpl
import com.google.firebase.Firebase
import com.google.firebase.initialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.pushy.sdk.Pushy
import org.koin.android.ext.koin.androidContext

class PartnerApplication : Application() {

    companion object Companion {

        const val DONE_NOTIFICATION_DEFAULT_CHANNEL = "done_notification_default_channel"
        const val DONE_NOTIFICATION_INFO_CHANNEL = "done_notification_info_channel"
        const val DONE_NOTIFICATION_SUCCESS_CHANNEL = "done_notification_success_channel"
        const val DONE_NOTIFICATION_ALERT_CHANNEL = "done_notification_alert_channel"
        const val DONE_NOTIFICATION_REJECTED_CHANNEL = "done_notification_rejected_channel"

        const val DONE_NOTIFICATION_BACKUP_CHANNEL = "done_notification_backup_channel"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        Firebase.initialize(applicationContext)

        Pushy.listen(this)
        Pushy.toggleForegroundService(true, this)
        registerForPushNotifications(this)

        createChannel(
            DONE_NOTIFICATION_DEFAULT_CHANNEL,
            R.raw.done_notification_default_sound,
            notificationName = getString(R.string.default_notification)
        )
        createChannel(
            DONE_NOTIFICATION_INFO_CHANNEL,
            R.raw.done_notification_info_sound,
            notificationName = getString(R.string.info_notification)
        )
        createChannel(
            DONE_NOTIFICATION_SUCCESS_CHANNEL,
            R.raw.done_notification_success_sound,
            notificationName = getString(R.string.success_notification)
        )
        createChannel(
            DONE_NOTIFICATION_ALERT_CHANNEL,
            R.raw.done_notification_alert_sound,
            notificationName = getString(R.string.alert_notification)
        )
        createChannel(
            DONE_NOTIFICATION_REJECTED_CHANNEL,
            R.raw.done_notification_rejected_sound,
            notificationName = getString(R.string.rejected_notification)
        )

        createChannel(
            DONE_NOTIFICATION_BACKUP_CHANNEL,
            R.raw.done_notification_backup_sound,
            notificationName = getString(R.string.backup_notification)
        )

        // Initialize Koin
        initKoin {
            androidContext(this@PartnerApplication)
        }
    }

    private fun registerForPushNotifications(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    // Register the device for notifications
                    val deviceToken = Pushy.register(context)

                    // Log token to logcat
                    Log.d("Pushy", "Pushy device token: $deviceToken")

                    // Return the token as the result
                    deviceToken
                } catch (exc: Exception) {
                    // Return the exception as the result
                    exc
                }
            }

            // Show the result on the main thread
            when (result) {
                is Exception -> {
                    Log.e("Pushy", result.message ?: "Error registering")
                    val message = result.message ?: "Unknown error occurred"
                    println("Pushy $message")
                }

                else -> {
                    val message = "Pushy device token: $result\n\n(copy from logcat)"
                    println("Pushy $message")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(
        channelId: String, channelSound: Int, notificationName: String
    ) {

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
        val sound = (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + channelSound).toUri()

        val channel = NotificationChannel(
            channelId,
            notificationName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setSound(sound, audioAttributes)
            enableVibration(true)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(
            LanguageServiceImpl(base).setCurrentLanguageToBaseContext()
        )
    }
}