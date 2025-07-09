package com.done.core.data.services.device_info

import com.done.core.domain.models.ip_info.IpInfo
import com.done.core.domain.services.device_info.DeviceInfoService
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.done.core.BuildConfig
import com.done.core.data.dto.ip_info.IpInfoDto
import com.done.core.data.dto.ip_info.toIpInfo
import com.done.core.data.services.api.KtorApiService
import com.done.core.domain.util.result.Result
import java.util.UUID

actual class DeviceInfoServiceImpl(
    private val context: Context,
    private val apiService: KtorApiService
) : DeviceInfoService {
    actual override fun getDeviceType(): String {
        return Build.MANUFACTURER + " " + Build.MODEL
    }

    @SuppressLint("HardwareIds")
    actual override fun getAndroidDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    actual override fun getBrandName(): String {
        return Build.BRAND
    }

    actual override fun getModelName(): String {
        return Build.MODEL
    }

    actual override fun getNetworkType(): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return "unknown"
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "unknown"

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wi-fi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "mobile data"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
            else -> "unknown"
        }
    }

    actual override fun getBatteryPercentage(): Int {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, intentFilter)

        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        if (level == -1 || scale == -1) return -1

        return (level * 100) / scale
    }

    actual override fun getAppInstallSource(): String? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val info = context.packageManager.getInstallSourceInfo(BuildConfig.APPLICATION_ID)
                info.installingPackageName
            } else {
                context.packageManager.getInstallerPackageName(BuildConfig.APPLICATION_ID)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private var sessionId = ""
    actual override fun getSessionId(): String {
        if (sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString()
        }
        return sessionId
    }

    private var ipInfo: IpInfo? = null
    actual override suspend fun getIpInfo(): IpInfo? {
        if (ipInfo == null) {
            val result = apiService.get<IpInfoDto>(
                baseUrl = "http://ip-api.com",
                route = "json/"
            )
            return when (result) {
                is com.done.core.domain.util.result.Result.Error<*> -> null
                is Result.Success<*> -> result.data?.toIpInfo()
            }
        }

        return ipInfo
    }

    actual override fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not required in Android 13 and below
        }
    }

    actual override fun canObserverLocation(): Boolean {
        if (!hasLocationPermission()) return false

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        return isGpsEnabled
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    actual override fun isBatteryStateBad(): Boolean {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }

        val batteryPct = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale
        } ?: 0

        return batteryPct <= 20
    }

    @SuppressLint("MissingPermission")
    actual override fun isFakingLocation(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = locationManager.getProviders(true)

        var isMock = false

        for (provider in providers) {
            val location = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                isMock = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    location.isMock
                } else {
                    location.isFromMockProvider
                }

                if (isMock) break
            }
        }

        return isMock
    }
}