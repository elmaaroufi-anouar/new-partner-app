package com.done.partner.presentation.product.product_options

import com.done.core.domain.util.result.NetworkError

sealed interface OptionsEvent {
    data class Error(val networkError: NetworkError?): OptionsEvent
}