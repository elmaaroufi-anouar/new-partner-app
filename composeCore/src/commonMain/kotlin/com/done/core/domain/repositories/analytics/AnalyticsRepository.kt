package com.done.core.domain.repositories.analytics

interface AnalyticsRepository {
    suspend fun logEvent(eventName: String, params: Map<String, Any>?)

    suspend fun setUser()
}