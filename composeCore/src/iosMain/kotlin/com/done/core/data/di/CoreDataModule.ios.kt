package com.done.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.done.core.data.repositories.analytics.AnalyticsRepositoryImpl
import com.done.core.data.services.datastore.DataStoreFactory
import com.done.core.domain.repositories.analytics.AnalyticsRepository
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformCoreDataModule = module {
    single<DataStore<Preferences>> { DataStoreFactory.create() }
    single<HttpClientEngine> { Darwin.create() }

    // Repositories
    singleOf(::AnalyticsRepositoryImpl).bind<AnalyticsRepository>()

    // Services
}
