package com.done.partner.presentation.permissions.util

interface AppPermissionsController {
    suspend fun getPermissionState(): PermissionState
    suspend fun providePermission()
    fun openAppSettings()
}

expect fun createPermissionsController(): AppPermissionsController
