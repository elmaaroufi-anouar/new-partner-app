package com.done.partner.data.services.play_services

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class DownloadCompletedReceiver : BroadcastReceiver(), KoinComponent {

    private val xapkInstaller: XapkInstaller by inject()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.DOWNLOAD_COMPLETE" && context != null) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id != -1L) {

                val fileName = "DonePlayServices.xapk"
                val downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val xapkFile = File(downloadPath, fileName)

                Log.d("XapkInstaller", "Download with ID $id finished ${xapkFile.absolutePath}")
                if (xapkFile.exists()) {
                    scope.launch {
                        xapkInstaller.installPackage(xapkFile)
                    }
                } else {
                    Log.e("XapkInstaller", "XAPK file not found at expected location: ${xapkFile.absolutePath}")
                }
            }
        }
    }
}