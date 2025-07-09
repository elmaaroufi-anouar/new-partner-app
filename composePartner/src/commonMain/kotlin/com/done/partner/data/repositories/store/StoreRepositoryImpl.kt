package com.done.partner.data.repositories.store

import com.done.core.BuildConfig
import com.done.core.data.util.ApiRoutes
import com.done.partner.data.dto.store.StoreDataDto
import com.done.partner.data.dto.store.StoreDto
import com.done.partner.data.dto.store.toStore
import com.done.core.data.services.api.KtorApiService
import com.done.core.domain.services.auth_response.AuthResponseService
import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.NetworkErrorName
import com.done.core.domain.util.result.Result
import com.done.partner.domain.models.store.Store
import com.done.partner.domain.repositories.store.StoreRepository
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException

class StoreRepositoryImpl(
    private val apiService: KtorApiService,
    private val authPayloadService: AuthResponseService
) : StoreRepository {

    override suspend fun getStore(storeId: String): Result<Store, NetworkError> {
        val location = authPayloadService.getStoreLocation() ?: return Result.Error(
            NetworkError(NetworkErrorName.UNKNOWN)
        )
        val result = apiService.get<StoreDto>(
            baseUrl = BuildConfig.FOOD_BASE_URL,
            route = ApiRoutes.storeById(storeId),
            headers = mapOf(
                "lat" to location.lat,
                "lon" to location.long
            )
        )

        if (result is Result.Error) {
            return Result.Error(result.error)
        }

        result.data?.let { storesDto ->
            val store = storesDto.toStore()

            return Result.Success(store)
        }
        return Result.Error(
            NetworkError(NetworkErrorName.SERIALIZATION_ERROR)
        )
    }

    override suspend fun getStores(): Result<List<Store>, NetworkError> {
        val result = apiService.get<StoreDataDto>(
            route = ApiRoutes.GET_STORES
        )

        if (result is Result.Error) {
            return Result.Error(result.error)
        }

        return try {
            val stores = result.data?.storeDtos ?: return Result.Error(
                NetworkError(NetworkErrorName.UNKNOWN)
            )
            stores.firstOrNull()?.toStore()?.let { firstStore ->
                authPayloadService.setStoreLocation(firstStore.latitude, firstStore.longitude)
            }
            Result.Success(stores.map { it.toStore() })
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Result.Error(NetworkError(NetworkErrorName.UNKNOWN))
        }
    }

    override suspend fun getStoreId(): String? = authPayloadService.getStoreId()

    override suspend fun getStoreName(): String? = authPayloadService.getStoreName()

    override suspend fun closeStore(storeId: String): Result<Unit, NetworkError> {
        val result = apiService.patch<Unit>(
            route = ApiRoutes.openCloseStore(storeId = storeId, state = "close")
        )

        return handleResult(result)
    }

    override suspend fun openStore(storeId: String): Result<Unit, NetworkError> {
        val result = apiService.patch<Unit>(
            route = ApiRoutes.openCloseStore(storeId = storeId, state = "open")
        )

        return handleResult(result)
    }

    private fun handleResult(
        result: Result<Unit, NetworkError>
    ): Result<Unit, NetworkError> {

        if (result is Result.Success) {
            return Result.Success(null)
        }

        return Result.Error(result.error)
    }
}