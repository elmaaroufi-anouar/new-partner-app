package com.done.core.domain.services.notifications

interface DeviceTokenRegisteringService {
    suspend fun hasToken(): Boolean

    fun registerDeviceToken()

    suspend fun registerDeviceToken(token: String, storeId: String? = null)
}