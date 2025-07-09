package com.done.partner.domain.services.play_services

interface PlayServicesService {

    suspend fun isPlayServicesUpdated(): Boolean

    suspend fun updatePlayServices(
        url: String, onPackageInstalled: (String) -> Unit
    )
}