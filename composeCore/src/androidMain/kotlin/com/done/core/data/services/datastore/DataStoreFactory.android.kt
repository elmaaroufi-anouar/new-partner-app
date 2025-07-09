package com.done.core.data.services.datastore


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.done.core.data.services.secure_storage.DATA_STORE_FILE_NAME
import com.done.core.data.services.secure_storage.DataStoreFactory

object DataStoreFactory {
    fun create(context: Context): DataStore<Preferences> {
        return DataStoreFactory.create {
            context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
        }
    }
}
