package com.done.partner.data.di

import androidx.core.app.NotificationManagerCompat
import com.done.core.data.repositories.crashlytics.CrashlyticsRepositoryImpl
import com.done.core.domain.repositories.crashlytics.CrashlyticsRepository
import com.done.partner.data.repositories.play_services.PlayServicesRepositoryImpl
import com.done.partner.data.services.play_services.PlayServicesServiceImpl
import com.done.partner.data.services.play_services.XapkInstaller
import com.done.partner.data.services.print.landi.LandiPrinterClient
import com.done.partner.data.services.print.newpas.AplsPrinterClient
import com.done.partner.data.services.print.sunmi.SunmiPrinterClient
import com.done.partner.domain.repositories.play_services.PlayServicesRepository
import com.done.partner.domain.services.play_services.PlayServicesService
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformPartnerDataModule = module {

    single { NotificationManagerCompat.from(androidApplication()) }

    // Repositories
    singleOf(::CrashlyticsRepositoryImpl).bind<CrashlyticsRepository>()
    singleOf(::PlayServicesRepositoryImpl).bind<PlayServicesRepository>()

    // Services
    singleOf(::PlayServicesServiceImpl).bind<PlayServicesService>()

    // Add other platform-specific dependencies here
    factoryOf(::SunmiPrinterClient)
    factoryOf(::AplsPrinterClient)
    factoryOf(::LandiPrinterClient)

    singleOf(::XapkInstaller)
}
