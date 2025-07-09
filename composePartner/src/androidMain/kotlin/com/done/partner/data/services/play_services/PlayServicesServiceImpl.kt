package com.done.partner.data.services.play_services

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.done.core.domain.services.secure_storage.SecureStorageService
import com.done.core.domain.services.notifications.DeviceTokenRegisteringService
import com.done.partner.domain.services.play_services.PlayServicesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PlayServicesServiceImpl(
    private val deviceTokenRegisteringService: DeviceTokenRegisteringService,
    private val secureStorageService: SecureStorageService,
    private val xapkInstaller: XapkInstaller,
    private val context: Context
) : PlayServicesService {

    private val tag = "XapkInstaller"

    private val packageInstallReceiver = PackageInstallReceiver()
    private var installationCallback: ((String) -> Unit)? = null

    init {
        packageInstallReceiver.onPackageInstalled = { packageName ->
            installationCallback?.invoke(packageName)
            Log.d(tag, "onPackageInstalled XAPK installed")
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        context.registerReceiver(packageInstallReceiver, filter)
    }

    override suspend fun isPlayServicesUpdated(): Boolean {
        val isPlayServicesUpdated = secureStorageService.getBoolean(
            KEY_IS_PLAY_SERVICES_UPDATED, false
        )

        val hasToken = deviceTokenRegisteringService.hasToken()

        return hasToken && isPlayServicesUpdated
    }

    override suspend fun updatePlayServices(
        url: String, onPackageInstalled: (String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            installationCallback = onPackageInstalled
            try {
                val fileName = "DonePlayServices.xapk"
                val downloadPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadPath, fileName)
                Log.d(tag, "Checking file at path: ${file.absolutePath}")
                Log.d(tag, "updatePlayServices file.exists() ${file.exists()}")
                if (file.exists()) {
                    Log.d(tag, "File $fileName already exists in Downloads, skipping download")
                    xapkInstaller.installPackage(file)
                } else {
                    val downloadManager =
                        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val request = DownloadManager.Request(url.toUri()).apply {
                        setTitle("Downloading $fileName")
                        setDescription("XAPK file download in progress")
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                        setMimeType("application/xapk")
                        setAllowedOverMetered(true)
                        setAllowedOverRoaming(true)
                    }
                    downloadManager.enqueue(request)
                    Log.d(tag, "updatePlayServices start $url")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(tag, "updatePlayServices error ${e.message}")
            }
        }
    }
}

const val KEY_IS_PLAY_SERVICES_UPDATED = "KEY_IS_PLAY_SERVICES_UPDATED"