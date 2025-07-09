package com.done.partner.domain.repositories.store

import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.Result
import com.done.partner.domain.models.store.Store

interface StoreRepository {

    suspend fun getStore(storeId: String): Result<Store, NetworkError>

    suspend fun getStores(): Result<List<Store>, NetworkError>

    suspend fun getStoreId(): String?

    suspend fun getStoreName(): String?

    suspend fun closeStore(storeId: String): Result<Unit, NetworkError>

    suspend fun openStore(storeId: String): Result<Unit, NetworkError>
}