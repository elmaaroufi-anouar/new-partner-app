package com.done.partner.presentation.permissions

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.partner.presentation.permissions.util.AppPermissionsController
import com.done.partner.presentation.permissions.util.PermissionDeniedAlwaysException
import com.done.partner.presentation.permissions.util.PermissionDeniedException
import com.done.partner.presentation.permissions.util.PermissionRequestCanceledException
import com.done.partner.presentation.permissions.util.PermissionState
import kotlinx.coroutines.launch

class PermissionsViewModel(
    private val permissionsController: AppPermissionsController
): ViewModel() {

    val state = mutableStateOf(PermissionState.NotDetermined)
        private set

    init {
        viewModelScope.launch {
            state.value = permissionsController.getPermissionState()
        }
    }

    fun provideOrRequestRemoteNotificationPermission() {
        viewModelScope.launch {
            try {
                permissionsController.providePermission()
                state.value = PermissionState.Granted
            } catch (e: PermissionDeniedAlwaysException) {
                state.value = PermissionState.DeniedAlways
            } catch (e: PermissionDeniedException) {
                state.value = PermissionState.Denied
            } catch (e: PermissionRequestCanceledException) {
                e.printStackTrace()
            }
        }
    }
}