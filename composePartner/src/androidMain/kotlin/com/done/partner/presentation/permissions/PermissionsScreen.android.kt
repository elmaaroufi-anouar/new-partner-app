package com.done.partner.presentation.permissions

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.done.core.presentation.core.ui.components.OnResumeCompose
import android.provider.Settings
import com.done.partner.presentation.permissions.util.permissionsGranted

@Composable
actual fun PermissionsScreen(onGranted: () -> Unit) {
    val activity = LocalActivity.current!!
    val context = LocalContext.current

    var launchAppSettings by remember { mutableStateOf(false) }

    val permission = Manifest.permission.POST_NOTIFICATIONS

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onGranted()
        } else {
            if (!shouldShowRequestPermissionRationale(activity, permission)) {
                launchAppSettings = true
            }
        }
    }

    var isGranted by remember {
        mutableStateOf(permissionsGranted(context))
    }

    OnResumeCompose {
        isGranted = permissionsGranted(context)

        if (isGranted) {
            onGranted()
        }
    }

    LaunchedEffect(true) {
        if (!isGranted) {
            if (!shouldShowRequestPermissionRationale(activity, permission)) {
                permissionLauncher.launch(permission)
            }
        } else {
            onGranted()
        }
    }

    if (!isGranted) {
        Permission(
            onEnable = {
                if (launchAppSettings) {
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts(
                            "package",
                            context.packageName,
                            null
                        )
                    ).also {
                        context.startActivity(it)
                    }
                    launchAppSettings = false
                } else {
                    permissionLauncher.launch(permission)
                }
            }
        )
    }
}