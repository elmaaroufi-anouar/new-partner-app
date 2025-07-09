package com.done.core.data.services.notifications

import com.done.core.domain.services.notifications.DeviceTokenRegisteringService

expect class DeviceTokenRegisteringServiceImpl: DeviceTokenRegisteringService {
    override suspend fun hasToken(): Boolean
    override fun registerDeviceToken()
    override suspend fun registerDeviceToken(token: String, storeId: String?)
}
