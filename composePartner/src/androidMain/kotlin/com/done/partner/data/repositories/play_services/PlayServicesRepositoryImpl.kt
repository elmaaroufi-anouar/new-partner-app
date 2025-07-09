package com.done.partner.data.repositories.play_services

import com.done.partner.domain.services.play_services.PlayServicesService
import com.done.partner.domain.repositories.play_services.PlayServicesRepository

class PlayServicesRepositoryImpl(
    private val playServicesService: PlayServicesService
) : PlayServicesRepository {

    override suspend fun isPlayServicesUpdated(): Boolean {
        return playServicesService.isPlayServicesUpdated()
    }

    override suspend fun updatePlayServices(
        url: String, onPackageInstalled: (String) -> Unit
    ) {
        playServicesService.updatePlayServices(
            url, onPackageInstalled
        )
    }
}