package com.done.partner.presentation.permissions.util

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import android.provider.Settings

actual class PermissionsController(
    private val context: Context = LocalContext.current
) {
    private val permission = POST_NOTIFICATIONS

    actual suspend fun requestPermission() {
        return suspendCoroutine { continuation ->
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    continuation.resume(Unit)
                } else {
                    if (!shouldShowRequestPermissionRationale(context as Activity, permission)) {
                        continuation.resumeWithException(DeniedAlways("Request denied forever")) // Considered as denied always
                    } else {
                        continuation.resumeWithException(Denied("request denied")) // Considered as denied
                    }
                }
            }

            launcher.launch(permission)
        }
    }

    actual fun openAppSettings() {
        context.startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
        )
    }
}