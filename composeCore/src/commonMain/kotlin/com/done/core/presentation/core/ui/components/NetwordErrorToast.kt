package com.done.core.presentation.core.ui.components

import com.done.core.domain.util.result.NetworkError

expect fun networkErrorToast(
    context: Any,
    networkError: NetworkError?,
    generalError: String,
    internetError: String,
)
