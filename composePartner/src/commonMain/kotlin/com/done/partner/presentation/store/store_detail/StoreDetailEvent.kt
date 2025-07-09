package com.done.partner.presentation.store.store_detail

import com.done.core.domain.util.result.NetworkError

sealed interface StoreDetailEvent {
    data class Error(val networkError: NetworkError?) : StoreDetailEvent
    data object OrderUpdated : StoreDetailEvent
    data class ScrollToFirstSelectedProduct(
        val section: Int, val product: Int
    ) : StoreDetailEvent
}