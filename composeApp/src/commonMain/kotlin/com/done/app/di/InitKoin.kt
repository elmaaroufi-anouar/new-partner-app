package com.done.app.di

import com.done.core.data.di.coreDataModule
import com.done.core.data.di.platformCoreDataModule
import com.done.partner.data.di.partnerDataModule
import com.done.partner.data.di.platformPartnerDataModule
import com.done.partner.presentation.core.di.partnerPresentationModule
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    org.koin.core.context.startKoin {
        config?.invoke(this)
        modules(
            appPresentationModule,
            platformCoreDataModule,
            coreDataModule,
            partnerDataModule,
            platformPartnerDataModule,
            partnerPresentationModule,
        )
    }
}
