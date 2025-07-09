package com.done.core.domain.services.auth_response

import com.done.core.domain.models.location.Location

interface AuthResponseService {
    suspend fun setAuthResponse(response: String)

    suspend fun setStoreLocation(lat: String, long: String)

    suspend fun removeAuthResponse()

    suspend fun getAuthToken(): String?

    suspend fun getStoreId(): String?

    suspend fun getStoreName(): String?

    suspend fun getStoreLocation(): Location?
}