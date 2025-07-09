package com.done.partner.data.repositories.auth

import com.done.core.data.services.api.KtorApiService
import com.done.core.data.util.ApiRoutes
import com.done.core.domain.repositories.event.TrackingRepository
import com.done.core.domain.services.auth_response.AuthResponseService
import com.done.core.domain.services.notifications.DeviceTokenRegisteringService
import com.done.core.domain.services.notifications.NotificationService
import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.NetworkErrorName
import com.done.core.domain.util.result.Result
import com.done.partner.domain.repositories.auth.AuthRepository

class AuthRepositoryImpl(
    private val apiService: KtorApiService,
    private val authResponseService: AuthResponseService,
    private val deviceTokenRegisteringService: DeviceTokenRegisteringService,
    private val notificationService: NotificationService,
    private val trackingRepository: TrackingRepository
) : AuthRepository {

    override suspend fun login(
        email: String, password: String
    ): Result<Unit, NetworkError> {

        val result = apiService.post<String>(
            route = ApiRoutes.LOGIN,
            body = hashMapOf(
                "email" to email,
                "password" to password
            )
        )

        if (result is Result.Error) {
            return Result.Error(result.error)
        }

        val responseBody = result.data
        if (responseBody != null) {
            authResponseService.setAuthResponse(responseBody)
            deviceTokenRegisteringService.registerDeviceToken()
            trackingRepository.trackLoginEvent(emailAddress = email, loginMethod = "email")
            authResponseService.getStoreId()?.let { notificationService.subscribeToUserTopic(it) }
            return Result.Success(null)
        }

        return Result.Error(NetworkError(NetworkErrorName.UNKNOWN))
    }

    override suspend fun isLoggedIn(): Boolean {
        return authResponseService.getAuthToken() != null
    }

    override suspend fun logout(isFromCTA: Boolean) {
        authResponseService.getStoreId()?.let { notificationService.unsubscribeFromTopics(it) }
        authResponseService.removeAuthResponse()
        trackingRepository.trackLogoutEvent(isFromCTA)
    }
}