package com.done.partner.presentation.permissions.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.done.partner.BuildConfig

fun permissionsGranted(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager?
    val hasBatteryPermission = powerManager
        ?.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID) == true

    val hasNotificationsPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat
            .checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    return hasNotificationsPermission
}