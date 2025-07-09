package com.done.core.data.repositories.crashlytics

import com.done.core.domain.repositories.crashlytics.CrashlyticsRepository

expect class CrashlyticsRepositoryImpl: CrashlyticsRepository {
    override fun setUser(user: String)
}
