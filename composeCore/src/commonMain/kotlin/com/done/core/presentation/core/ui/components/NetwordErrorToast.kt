package com.done.core.presentation.core.ui.components

import androidx.compose.runtime.Composable
import com.done.core.domain.util.result.NetworkError

@Composable
expect fun networkErrorToast(
    networkError: NetworkError?,
)
