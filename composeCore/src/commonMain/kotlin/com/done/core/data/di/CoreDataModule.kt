package com.done.core.data.di

import com.done.core.data.repositories.event.TrackingRepositoryImpl
import com.done.core.data.services.api.HttpClientFactory
import com.done.core.data.services.api.KtorApiService
import com.done.core.data.services.auth_response.AuthResponseServiceImpl
import com.done.core.data.services.secure_storage.DataStoreFactory
import com.done.core.data.services.secure_storage.SecureStorageServiceImpl
import com.done.core.domain.repositories.event.TrackingRepository
import com.done.core.domain.services.auth_response.AuthResponseService
import com.done.core.domain.services.secure_storage.SecureStorageService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule = module {

    // DataStore
    single { DataStoreFactory.create(get()) }

    // HttpClient
    single { HttpClientFactory.create(get()) }

    // Repositories

    singleOf(::TrackingRepositoryImpl).bind<TrackingRepository>()


    // Services

    singleOf(::KtorApiService)

    singleOf(::AuthResponseServiceImpl).bind<AuthResponseService>()

    singleOf(::SecureStorageServiceImpl).bind<SecureStorageService>()

}