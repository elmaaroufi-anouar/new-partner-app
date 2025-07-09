package com.done.app.di

import com.done.app.CoreViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appPresentationModule = module {
    viewModelOf(::CoreViewModel)
}
