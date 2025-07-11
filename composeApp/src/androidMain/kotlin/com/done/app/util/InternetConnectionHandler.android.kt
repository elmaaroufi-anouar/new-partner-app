package com.done.app.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun provideInternetConnectionHandler(): InternetConnectionHandler {
    val context = LocalContext.current

    return remember { AndroidInternetConnectionHandler(context) }
}

class AndroidInternetConnectionHandler(
    private val context: Context
) : InternetConnectionHandler {

    override fun openConnectionSettings() {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
