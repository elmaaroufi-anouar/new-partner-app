package com.done.partner.presentation.store.product_detail

import com.done.core.domain.util.result.NetworkError

sealed interface ProductDetailEvent {
    data class Error(val networkError: NetworkError?) : ProductDetailEvent
    data object HighlightRequiredFields : ProductDetailEvent
}