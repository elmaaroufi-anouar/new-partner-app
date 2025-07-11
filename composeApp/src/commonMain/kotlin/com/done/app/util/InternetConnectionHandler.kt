package com.done.app.util

import androidx.compose.runtime.Composable

interface InternetConnectionHandler {
    fun openConnectionSettings()
}

@Composable
expect fun provideInternetConnectionHandler(): InternetConnectionHandler
