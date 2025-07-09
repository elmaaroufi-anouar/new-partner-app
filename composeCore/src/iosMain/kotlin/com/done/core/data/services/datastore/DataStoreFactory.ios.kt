package com.done.core.data.services.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.done.core.data.services.secure_storage.DATA_STORE_FILE_NAME
import com.done.core.data.services.secure_storage.DataStoreFactory
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

object DataStoreFactory {
    @OptIn(ExperimentalForeignApi::class)
    fun create(): DataStore<Preferences> {
        return DataStoreFactory.create {
            val directory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            requireNotNull(directory).path + "/$DATA_STORE_FILE_NAME"
        }
    }
}