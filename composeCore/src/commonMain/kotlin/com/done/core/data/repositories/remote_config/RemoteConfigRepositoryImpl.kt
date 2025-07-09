package com.done.core.data.repositories.remote_config

import com.done.core.domain.models.config.Config
import com.done.core.domain.repositories.remote_config.RemoteConfigRepository

expect class RemoteConfigRepositoryImpl : RemoteConfigRepository {
    override suspend fun getConfig(): Config?
}
