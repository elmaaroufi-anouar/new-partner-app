package com.done.partner.domain.repositories.play_services

interface PlayServicesRepository {

    suspend fun isPlayServicesUpdated(): Boolean

    suspend fun updatePlayServices(
        url: String, onPackageInstalled: (String) -> Unit
    )
}