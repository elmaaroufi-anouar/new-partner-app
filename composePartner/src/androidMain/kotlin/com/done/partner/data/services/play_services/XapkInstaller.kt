package com.done.partner.data.services.play_services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipFile

class XapkInstaller(
    private val context: Context
) {
    private val tag = "XapkInstaller"

    fun installPackage(xapkFile: File) {
        try {
            Log.d(tag, "installPackage")
            // Check if the file exists
            if (!xapkFile.exists()) {
                Log.e(tag, "XAPK file does not exist")
                return
            }

            // Create temporary directory for extraction
            val tempDir = File(context.cacheDir, "xapk_temp_${System.currentTimeMillis()}")
            tempDir.mkdir()

            // Extract the XAPK file (which is essentially a ZIP file)
            extractXapkFile(xapkFile, tempDir)

            // Parse the manifest.json file
            val manifestFile = File(tempDir, "manifest.json")
            if (!manifestFile.exists()) {
                Log.e(tag, "manifest.json not found in XAPK")
                tempDir.deleteRecursively()
                return
            }

            Log.d(tag, "manifest.json found")
            val manifestJson = JSONObject(manifestFile.readText())
            val packageName = manifestJson.getString("package_name")

            // Get the list of split APKs
            val splitApks = mutableListOf<File>()
            val splitApksArray = manifestJson.getJSONArray("split_apks")

            Log.d(tag, "splitApksArray")
            for (i in 0 until splitApksArray.length()) {
                val splitApkObj = splitApksArray.getJSONObject(i)
                val fileName = splitApkObj.getString("file")
                val apkFile = File(tempDir, fileName)

                if (apkFile.exists()) {
                    splitApks.add(apkFile)
                } else {
                    Log.e(tag, "APK file not found: $fileName")
                }
            }

            // Install the package using PackageInstaller
            installSplitApks(splitApks, packageName)

            // Clean up temporary files
            tempDir.deleteRecursively()
        } catch (e: Exception) {
            Log.e(tag, "Error installing XAPK", e)
        }
    }

    private fun extractXapkFile(xapkFile: File, destDir: File) {
        try {
            Log.d(tag, "extractXapkFile")
            ZipFile(xapkFile).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    Log.d(tag, "extractXapkFile ${entry.name}")
                    val outFile = File(destDir, entry.name)
                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        zip.getInputStream(entry).use { input ->
                            outFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("Range")
    private fun installSplitApks(apkFiles: List<File>, packageName: String) {
        Log.d(tag, "installSplitApks")
        val packageInstaller = context.packageManager.packageInstaller

        // Create a new session
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        params.setAppPackageName(packageName)

        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)

        try {
            // Write each APK to the session
            for (apkFile in apkFiles) {
                Log.d(tag, "installSplitApks apkFile ${apkFile.name}")
                val inputStream = FileInputStream(apkFile)
                val outputStream = session.openWrite(apkFile.name, 0, apkFile.length())

                val buffer = ByteArray(8192)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                session.fsync(outputStream)
                outputStream.close()
                inputStream.close()
            }

            Log.d(tag, "installSplitApks Create a broadcast receiver to get the result")
            // Create a broadcast receiver to get the result
            val intent = Intent(context, InstallResultReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                sessionId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            // Commit the session
            session.commit(pendingIntent.intentSender)
        } catch (e: Exception) {
            Log.e(tag, "Error during installation", e)
            session.abandon()
        } finally {
            session.close()
        }
    }
}

class InstallResultReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
        val message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)

        when (status) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                Log.d("XapkInstaller", "InstallResultReceiver STATUS_PENDING_USER_ACTION")

                val confirmIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(Intent.EXTRA_INTENT)
                }

                if (confirmIntent != null) {
                    confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(confirmIntent)
                        Log.d("XapkInstaller", "Started confirmation intent")
                    } catch (e: Exception) {
                        Log.e("XapkInstaller", "Failed to start confirmation intent: ${e.message}")
                        e.printStackTrace()
                    }
                } else {
                    Log.e("XapkInstaller", "Confirmation intent is null")
                }
            }
            PackageInstaller.STATUS_SUCCESS -> {
                Log.d("MultiApkXapkInstaller", "Installation successful")
            }
            else -> {
                Log.e("MultiApkXapkInstaller", "Installation failed: $message")
            }
        }
    }
}