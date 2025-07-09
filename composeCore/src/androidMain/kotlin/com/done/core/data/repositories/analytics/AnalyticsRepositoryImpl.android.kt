package com.done.core.data.repositories.analytics

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.done.core.domain.services.auth_response.AuthResponseService
import com.done.core.domain.repositories.analytics.AnalyticsRepository
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.mp.KoinPlatform

actual class AnalyticsRepositoryImpl(
    private val authPayloadService: AuthResponseService,
) : AnalyticsRepository {

    private val context: Context = KoinPlatform.getKoin().get()
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    @SuppressLint("HardwareIds")
    actual override suspend fun logEvent(eventName: String, params: Map<String, Any>?) {
//        if (BuildConfig.DEBUG) {
//            println("FirebaseAnalytics $eventName $params")
//            return
//        }
        val allParams = params?.plus(getOtherParams())
        val bundle: Bundle? = allParams?.run {
            val bundle = Bundle()
            this.forEach { (t, u) ->
                when (u) {
                    is String -> bundle.putString(t, u)
                    is Int -> bundle.putInt(t, u)
                    is Long -> bundle.putLong(t, u)
                    is Float -> bundle.putFloat(t, u)
                    is Double -> bundle.putDouble(t, u)
                    else -> bundle.putString(t, u.toString())
                }
            }
            bundle
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    @SuppressLint("HardwareIds")
    actual override suspend fun setUser() {
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val storeId = authPayloadService.getStoreId()

        firebaseAnalytics.setUserId(storeId ?: deviceId)
        firebaseAnalytics.setUserProperty("storeId", storeId ?: "")
        firebaseAnalytics.setUserProperty("deviceId", deviceId ?: "")
    }

    @SuppressLint("HardwareIds")
    private suspend fun getOtherParams(): Map<String, Any?> {
        try {
            val storeId = authPayloadService.getStoreId()
            val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName

            return mapOf(
                "storeId" to storeId,
                "deviceId" to deviceId,
                "deviceType" to Build.MANUFACTURER + " " + Build.MODEL,
                "androidVersion" to Build.VERSION.RELEASE,
                "sdkVersion" to Build.VERSION.SDK_INT,
                "appVersion" to appVersion
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyMap()
        }
    }
}