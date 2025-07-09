package com.done.core.data.services.device_info

actual class DeviceInfoServiceImpl : com.done.core.domain.services.device_info.DeviceInfoService {
    actual override fun getDeviceType(): String {
        TODO("Not yet implemented")
    }

    actual override fun getAndroidDeviceId(): String {
        TODO("Not yet implemented")
    }

    actual override fun getBrandName(): String {
        TODO("Not yet implemented")
    }

    actual override fun getModelName(): String {
        TODO("Not yet implemented")
    }

    actual override fun getNetworkType(): String {
        TODO("Not yet implemented")
    }

    actual override fun getBatteryPercentage(): Int {
        TODO("Not yet implemented")
    }

    actual override fun getAppInstallSource(): String? {
        TODO("Not yet implemented")
    }

    actual override fun getSessionId(): String {
        TODO("Not yet implemented")
    }

    actual override suspend fun getIpInfo(): com.done.core.domain.models.ip_info.IpInfo? {
        TODO("Not yet implemented")
    }

    actual override fun hasNotificationPermission(): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun canObserverLocation(): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun isBatteryStateBad(): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun isFakingLocation(): Boolean {
        TODO("Not yet implemented")
    }
}