package com.done.partner.presentation.permissions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import kotlinx.coroutines.launch

class PermissionsViewModel(
    private val permissionsController: PermissionsController
): ViewModel() {

    val state = mutableStateOf(PermissionState.NotDetermined)
        private set

    init {
        viewModelScope.launch {
            state.value = permissionsController.getPermissionState(Permission.REMOTE_NOTIFICATION)
        }
    }

    fun provideOrRequestRemoteNotificationPermission() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission(Permission.REMOTE_NOTIFICATION)
                state.value = PermissionState.Granted
            } catch (e: DeniedAlwaysException) {
                state.value = PermissionState.DeniedAlways
            } catch (e: DeniedException) {
                state.value = PermissionState.Denied
            } catch (e: RequestCanceledException) {
                e.printStackTrace()
            }
        }
    }
}