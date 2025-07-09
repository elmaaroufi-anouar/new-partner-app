package com.done.partner.data.services.play_services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.done.core.domain.services.secure_storage.SecureStorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class PackageInstallReceiver : BroadcastReceiver(), KoinComponent {

    private val secureStorageService: SecureStorageService by inject()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    var onPackageInstalled: ((String) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart
        if (intent.action == Intent.ACTION_PACKAGE_ADDED ||
            intent.action == Intent.ACTION_PACKAGE_REPLACED
        ) {
            packageName?.let {
                scope.launch {
                    secureStorageService.putBoolean(KEY_IS_PLAY_SERVICES_UPDATED, true)
                }
                onPackageInstalled?.invoke(it)
            }
        }
    }
}