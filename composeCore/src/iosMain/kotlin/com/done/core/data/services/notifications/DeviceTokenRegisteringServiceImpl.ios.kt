package com.done.core.data.services.notifications

import com.done.core.domain.services.notifications.DeviceTokenRegisteringService

actual class DeviceTokenRegisteringServiceImpl :
    DeviceTokenRegisteringService {
    actual override suspend fun hasToken(): Boolean {
        TODO("Not yet implemented")
    }

    actual override fun registerDeviceToken() {
    }

    actual override suspend fun registerDeviceToken(token: String, storeId: String?) {
    }
}