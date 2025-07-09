package com.done.core.data.services.auth_response

import com.done.core.data.util.jsonWithUnknownKeys
import com.done.core.domain.models.location.Location
import com.done.core.domain.services.auth_response.AuthResponseService
import com.done.core.domain.services.secure_storage.SecureStorageService
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AuthResponseServiceImpl(
    private val secureStorageService: SecureStorageService
) : AuthResponseService {

    override suspend fun setAuthResponse(response: String) {
        secureStorageService.putString(KEY_LOGIN_RESPONSE, response)
        val responseJson = jsonWithUnknownKeys.decodeFromString<JsonObject>(response)

        try {
            val lat = responseJson["store"]?.jsonObject?.get("latitude")?.jsonPrimitive?.content
            val long = responseJson["store"]?.jsonObject?.get("longitude")?.jsonPrimitive?.content
            if (lat == null || long == null) return
            setStoreLocation(lat = lat, long = long)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun setStoreLocation(lat: String, long: String) {
        secureStorageService.putString(KEY_LAT, lat)
        secureStorageService.putString(KEY_LONG, long)
    }

    override suspend fun removeAuthResponse() {
        secureStorageService.removeString(KEY_LOGIN_RESPONSE)
    }

    override suspend fun getAuthToken(): String? {
        val authResponse = secureStorageService.getString(KEY_LOGIN_RESPONSE) ?: return null

        try {
            val responseJson = jsonWithUnknownKeys.decodeFromString<JsonObject>(authResponse)
            val token = responseJson["token"]?.jsonPrimitive?.content

            return token
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun getStoreId(): String? {
        val authResponse = secureStorageService.getString(KEY_LOGIN_RESPONSE) ?: return null
        try {
            val responseJson = jsonWithUnknownKeys.decodeFromString<JsonObject>(authResponse)
            val storeId = responseJson["store"]?.jsonObject?.get("id")?.jsonPrimitive?.content
            return storeId
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun getStoreName(): String? {
        val authResponse = secureStorageService.getString(KEY_LOGIN_RESPONSE) ?: return null
        try {
            val responseJson = jsonWithUnknownKeys.decodeFromString<JsonObject>(authResponse)
            val storeName = responseJson["store"]?.jsonObject?.get("slug")?.jsonPrimitive?.content
            return storeName
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun getStoreLocation(): Location? {
        val lat = secureStorageService.getString(KEY_LAT) ?: return null
        val long = secureStorageService.getString(KEY_LONG) ?: return null
        return Location(lat = lat, long = long)
    }


    companion object {
        private const val KEY_LOGIN_RESPONSE = "KEY_LOGIN_RESPONSE"
        private const val KEY_LAT = "KEY_LAT"
        private const val KEY_LONG = "KEY_LONG"
    }
}
