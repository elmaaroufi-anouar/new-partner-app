package com.done.partner.presentation.product.products

import com.done.core.domain.util.result.NetworkError

sealed interface ProductsEvent {
    data class Error(val networkError: NetworkError?): ProductsEvent
}