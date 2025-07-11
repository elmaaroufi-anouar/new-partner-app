package com.done.core.presentation.core.util

import androidx.compose.runtime.Composable

interface InternetConnectionHandler {
    fun openConnectionSettings()
}

@Composable
expect fun provideInternetConnectionHandler(): InternetConnectionHandler
