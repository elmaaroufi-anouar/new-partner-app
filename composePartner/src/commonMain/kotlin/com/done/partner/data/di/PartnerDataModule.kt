package com.done.partner.data.di

import com.done.partner.domain.repositories.auth.AuthRepository
import com.done.partner.domain.repositories.order.OrderRepository
import com.done.partner.domain.repositories.prodcuts.ProductsRepository
import com.done.partner.domain.repositories.settings.SettingsRepository
import com.done.partner.domain.repositories.store.StoreRepository
import com.done.partner.data.repositories.auth.AuthRepositoryImpl
import com.done.partner.data.repositories.order.OrderRepositoryImpl
import com.done.partner.data.repositories.prodcuts.ProductsRepositoryImpl
import com.done.partner.data.repositories.settings.SettingsRepositoryImpl
import com.done.partner.data.repositories.store.StoreRepositoryImpl
import com.done.core.data.repositories.patterns_validator.PatternsValidatorRepositoryImpl
import com.done.partner.data.services.print.PrinterServiceImpl
import com.done.partner.domain.services.print.PrinterService
import com.done.core.domain.repositories.patterns_validator.PatternsValidatorRepository
import com.done.partner.domain.util.OrderNotificationsSender
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformPartnerDataModule: Module

val partnerDataModule = module {

    singleOf(::OrderNotificationsSender)

    // Repositories

    factoryOf(::AuthRepositoryImpl).bind<AuthRepository>()

    singleOf(::StoreRepositoryImpl).bind<StoreRepository>()

    singleOf(::OrderRepositoryImpl).bind<OrderRepository>()

    singleOf(::ProductsRepositoryImpl).bind<ProductsRepository>()

    singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()

    singleOf(::PatternsValidatorRepositoryImpl).bind<PatternsValidatorRepository>()

    // Services
    singleOf(::PrinterServiceImpl).bind<PrinterService>()
}