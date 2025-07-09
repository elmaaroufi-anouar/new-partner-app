package com.done.core.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.done.core.data.repositories.analytics.AnalyticsRepositoryImpl
import com.done.core.data.repositories.remote_config.RemoteConfigRepositoryImpl
import com.done.core.data.services.datastore.DataStoreFactory
import com.done.core.data.services.device_info.DeviceInfoServiceImpl
import com.done.core.data.services.language.LanguageServiceImpl
import com.done.core.data.services.notifications.DeviceTokenRegisteringServiceImpl
import com.done.core.data.services.notifications.NotificationServiceImpl
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import com.done.core.domain.repositories.analytics.AnalyticsRepository
import com.done.core.domain.repositories.remote_config.RemoteConfigRepository
import com.done.core.domain.services.device_info.DeviceInfoService
import com.done.core.domain.services.language.LanguageService
import com.done.core.domain.services.notifications.DeviceTokenRegisteringService
import com.done.core.domain.services.notifications.NotificationService
import org.koin.dsl.bind

actual val platformCoreDataModule = module {
    single<DataStore<Preferences>> { DataStoreFactory.create(androidContext()) }
    single<HttpClientEngine> { OkHttp.create() }

    // Repositories
    singleOf(::AnalyticsRepositoryImpl).bind<AnalyticsRepository>()
    singleOf(::RemoteConfigRepositoryImpl).bind<RemoteConfigRepository>()

    // Services
    singleOf(::DeviceInfoServiceImpl).bind<DeviceInfoService>()
    singleOf(::LanguageServiceImpl).bind<LanguageService>()
    singleOf(::NotificationServiceImpl).bind<NotificationService>()
    singleOf(::DeviceTokenRegisteringServiceImpl).bind<DeviceTokenRegisteringService>()
}
