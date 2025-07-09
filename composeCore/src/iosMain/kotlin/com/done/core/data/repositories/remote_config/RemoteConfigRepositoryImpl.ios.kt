package com.done.core.data.repositories.remote_config

import com.done.core.domain.models.config.Config
import com.done.core.domain.repositories.remote_config.RemoteConfigRepository

actual class RemoteConfigRepositoryImpl : RemoteConfigRepository {
    actual override suspend fun getConfig(): Config? {
        TODO("Not yet implemented")
    }
}