package com.done.partner.presentation.permissions.util

import androidx.compose.runtime.remember
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.PermissionState as MokoPermissionState
import dev.icerock.moko.permissions.DeniedAlwaysException as MokoDeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException as MokoDeniedException
import dev.icerock.moko.permissions.RequestCanceledException as MokoRequestCanceledException

actual fun createPermissionsController(): AppPermissionsController {
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }

    return AndroidPermissionsController(controller)
}

class AndroidPermissionsController(
    private val permissionsController: PermissionsController
) : AppPermissionsController {
    override suspend fun getPermissionState(): PermissionState {
        val permissionState = permissionsController.getPermissionState(Permission.REMOTE_NOTIFICATION)

        return when(permissionState) {
            MokoPermissionState.Granted -> PermissionState.Granted
            MokoPermissionState.Denied -> PermissionState.Denied
            MokoPermissionState.DeniedAlways -> PermissionState.DeniedAlways
            MokoPermissionState.NotDetermined -> PermissionState.NotDetermined
            MokoPermissionState.NotGranted -> PermissionState.NotGranted
        }
    }

    override suspend fun providePermission() {
        try {
            permissionsController.providePermission(Permission.REMOTE_NOTIFICATION)
        } catch (e: MokoDeniedAlwaysException) {
            throw PermissionDeniedAlwaysException()
        } catch (e: MokoDeniedException) {
            throw PermissionDeniedException()
        } catch (e: MokoRequestCanceledException) {
            throw PermissionRequestCanceledException()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun openAppSettings() {
        permissionsController.openAppSettings()
    }

}
