package com.done.core.domain.services.device_info

import com.done.core.domain.models.ip_info.IpInfo

interface DeviceInfoService {
    fun getDeviceType(): String

    fun getAndroidDeviceId(): String

    fun getBrandName(): String

    fun getModelName(): String

    fun getNetworkType(): String

    fun getBatteryPercentage(): Int

    fun getAppInstallSource(): String?

    fun getSessionId(): String

    suspend fun getIpInfo(): IpInfo?

    fun hasNotificationPermission(): Boolean

    fun canObserverLocation(): Boolean

    fun isBatteryStateBad(): Boolean

    fun isFakingLocation(): Boolean
    fun getReleaseVersion(): String
}

