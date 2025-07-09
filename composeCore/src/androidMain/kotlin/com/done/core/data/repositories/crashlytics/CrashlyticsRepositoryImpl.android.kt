package com.done.core.data.repositories.crashlytics

import com.done.core.domain.repositories.crashlytics.CrashlyticsRepository
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

actual class CrashlyticsRepositoryImpl : CrashlyticsRepository {
    actual override fun setUser(user: String) {
        Firebase.crashlytics.setUserId(user)
    }
}