package com.done.core.data.repositories.remote_config

import com.done.core.domain.models.config.Config
import com.done.core.domain.repositories.remote_config.RemoteConfigRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

actual class RemoteConfigRepositoryImpl : RemoteConfigRepository {
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    override suspend fun getConfig(): Config? = suspendCancellableCoroutine { continuation ->
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                scope.launch {
                    continuation.resume(getProductionConfig())
                }
            } else {
                continuation.resume(null)
            }
        }
    }

    private fun getProductionConfig(): Config? {
        return Config(
            partnerAndroidActivateFCM = remoteConfig.getString("partner_android_activate_FCM") == "true",
            partnerAndroidPlayServicesUrl = remoteConfig.getString("partner_android_play_services_url"),
            partnerAndroidUpdateUrl = remoteConfig.getString("partner_android_update_url"),
            partnerAndroidVersion = remoteConfig.getLong("partner_android_version").toInt(),
            partnerSendAppEvents = remoteConfig.getBoolean("partner_send_app_events")
        )
    }
}