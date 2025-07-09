package com.done.core.data.repositories.analytics

import com.done.core.domain.repositories.analytics.AnalyticsRepository
import com.done.core.domain.services.auth_response.AuthResponseService

actual class AnalyticsRepositoryImpl(
    private val authPayloadService: AuthResponseService,
): AnalyticsRepository {
    actual override suspend fun logEvent(
        eventName: String,
        params: Map<String, Any>?
    ) {
        TODO("Not yet implemented")
    }

    actual override suspend fun setUser() {
        TODO("Not yet implemented")
    }

}