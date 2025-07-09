package com.done.core.domain.repositories.remote_config

import com.done.core.domain.models.config.Config

interface RemoteConfigRepository {
    suspend fun getConfig(): Config?
}