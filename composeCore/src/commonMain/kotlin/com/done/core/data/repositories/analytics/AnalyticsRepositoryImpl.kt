package com.done.core.data.repositories.analytics

import com.done.core.domain.services.auth_response.AuthResponseService
import com.done.core.domain.repositories.analytics.AnalyticsRepository

expect class AnalyticsRepositoryImpl : AnalyticsRepository {
    override suspend fun logEvent(eventName: String, params: Map<String, Any>?)
    override suspend fun setUser()
}