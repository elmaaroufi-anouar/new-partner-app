package com.done.partner.presentation.core.di

import com.done.partner.presentation.login.LoginViewModel
import com.done.partner.presentation.order_list.OrderListViewModel
import com.done.partner.presentation.product.products.ProductsViewModel
import com.done.partner.presentation.settings.SettingsViewModel
import com.done.partner.presentation.order_details.OrderDetailsViewModel
import com.done.partner.presentation.product.product_options.ProductOptionsViewModel
import com.done.partner.presentation.store.product_detail.ProductDetailViewModel
import com.done.partner.presentation.store.store_detail.StoreDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val partnerPresentationModule = module {

    viewModelOf(::LoginViewModel)

    viewModelOf(::OrderListViewModel)

    viewModelOf(::ProductsViewModel)

    viewModelOf(::OrderDetailsViewModel)

    viewModelOf(::SettingsViewModel)

    viewModelOf(::StoreDetailViewModel)

    viewModelOf(::ProductDetailViewModel)

    viewModelOf(::ProductOptionsViewModel)
}