package com.done.core.data.services.notifications

import com.done.core.data.util.ApiRoutes
import com.done.core.domain.services.notifications.DeviceTokenRegisteringService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import com.done.core.data.services.api.KtorApiService
import com.done.core.domain.services.auth_response.AuthResponseService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

actual class DeviceTokenRegisteringServiceImpl(
    private val apiService: KtorApiService,
    private val authResponseService: AuthResponseService
) :
    DeviceTokenRegisteringService {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    actual override suspend fun hasToken(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    task.exception?.printStackTrace()
                    continuation.resumeWith(Result.success(false))
                } else {
                    continuation.resumeWith(Result.success(true))
                }
            }
        }
    }

    actual override fun registerDeviceToken() {

        val storeId = runBlocking { authResponseService.getStoreId() }

        if (storeId != null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(
                OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        task.exception?.printStackTrace()
                        return@OnCompleteListener
                    }

                    println("FirebaseMessaging.token ${task.result}")
                    val token = task.result
                    scope.launch {
                        registerDeviceToken(token, storeId)
                    }
                }
            )
        }
    }

    actual override suspend fun registerDeviceToken(token: String, storeId: String?) {
        if (storeId == null) {
            authResponseService.getStoreId()?.let { nonnullStoreId ->
                registerTokenInServer(token, nonnullStoreId)
            }
        } else {
            registerTokenInServer(token, storeId)
        }
    }

    private suspend fun registerTokenInServer(token: String, storeId: String) {
        println("registerTokenInServer $token")
        apiService.patch<Unit>(
            route = ApiRoutes.registerTokenInServer(storeId = storeId),
            body = hashMapOf(
                "device_token" to token,
            )
        )
    }
}