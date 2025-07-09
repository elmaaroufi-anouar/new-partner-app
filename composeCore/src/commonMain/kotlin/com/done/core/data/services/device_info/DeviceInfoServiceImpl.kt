package com.done.core.data.services.device_info


import com.done.core.domain.models.ip_info.IpInfo
import com.done.core.domain.services.device_info.DeviceInfoService

expect class DeviceInfoServiceImpl : DeviceInfoService {
    override fun getDeviceType(): String
    override fun getAndroidDeviceId(): String
    override fun getBrandName(): String
    override fun getModelName(): String
    override fun getNetworkType(): String
    override fun getBatteryPercentage(): Int
    override fun getAppInstallSource(): String?
    override fun getSessionId(): String
    override suspend fun getIpInfo(): IpInfo?
    override fun hasNotificationPermission(): Boolean
    override fun canObserverLocation(): Boolean
    override fun isBatteryStateBad(): Boolean
    override fun isFakingLocation(): Boolean
}
