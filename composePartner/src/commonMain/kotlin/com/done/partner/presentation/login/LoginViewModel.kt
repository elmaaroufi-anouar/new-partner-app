package com.done.partner.presentation.login

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.core.domain.repositories.analytics.AnalyticsRepository
import com.done.core.domain.repositories.crashlytics.CrashlyticsRepository
import com.done.partner.domain.repositories.auth.AuthRepository
import com.done.core.domain.repositories.remote_config.RemoteConfigRepository
import com.done.core.domain.repositories.patterns_validator.PatternsValidatorRepository
import com.done.core.domain.services.notifications.NotificationService
import com.done.core.domain.util.result.Result
import com.done.core.presentation.core.util.toMap
import com.done.partner.domain.repositories.play_services.PlayServicesRepository
import com.done.core.presentation.core.util.UiAction
import com.done.partner.platform
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val patternsValidatorRepository: PatternsValidatorRepository,
    private val playServicesRepository: PlayServicesRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val analyticsRepository: AnalyticsRepository,
    private val crashlyticsRepository: CrashlyticsRepository,
    private val notificationService: NotificationService
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<LoginEvent>()
    val event = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            notificationService.unsubscribeFromTopics("")
        }
        viewModelScope.launch {
            snapshotFlow { state.value.email.text }.collectLatest { email ->
                checkCanLogin(email.toString().trim(), state.value.password.text.toString())
            }
        }

        viewModelScope.launch {
            snapshotFlow { state.value.password.text }.collectLatest { password ->
                checkCanLogin(state.value.email.text.toString().trim(), password.toString())
            }
        }
    }

    fun onAction(action: LoginAction) {
        viewModelScope.launch {
            analyticsRepository.logEvent(UiAction.LoginAction, action.toMap())
        }
        when (action) {
            LoginAction.OnLoginClick -> login()

            is LoginAction.OnTogglePasswordVisibilityClick -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !state.value.isPasswordVisible
                    )
                }
            }

            is LoginAction.OnCheckCanLoginTest -> {
                checkCanLogin(action.email, action.password)
            }

            is LoginAction.OnLoad -> {
                viewModelScope.launch {
                    val activateFCM = remoteConfigRepository.getConfig()?.partnerAndroidActivateFCM
                    println("XapkInstaller activateFCM: $activateFCM")

                    _state.update {
                        it.copy(activateFCM = activateFCM)
                    }
                    if (platform() == "Android" && state.value.activateFCM == true) {
                        checkPlayServices()
                    }
                }
            }
        }
    }

    private fun checkCanLogin(email: String, password: String) {
        _state.update {
            it.copy(
                canLogin = email.isNotEmpty()
                        && password.isNotEmpty()
                        && patternsValidatorRepository.matches(email)
                        && !state.value.isLoggingIn
            )
        }
    }

    private fun checkPlayServices() {
        viewModelScope.launch {
            if (state.value.isUpdatingPlayServices || playServicesRepository.isPlayServicesUpdated()) {
                return@launch
            }

            val url = remoteConfigRepository.getConfig()?.partnerAndroidPlayServicesUrl ?: state.value.playServicesUrl
            _state.update {
                it.copy(isUpdatingPlayServices = true)
            }
            playServicesRepository.updatePlayServices(
                url = url,
                onPackageInstalled = {
                    _state.update {
                        it.copy(isUpdatingPlayServices = false)
                    }
                    viewModelScope.launch {
                        Log.d(
                            "XapkInstaller", "install finished, hasToke: ${playServicesRepository.isPlayServicesUpdated()}"
                        )
                        eventChannel.send(LoginEvent.RestartApp)
                    }
                }
            )
        }
    }

    private fun login() {
        val email = state.value.email.text.toString().trim()
        val password = state.value.password.text.toString()

        viewModelScope.launch {
            _state.update { it.copy(isLoggingIn = true) }

            val result = authRepository.login(
                email = email, password = password,
            )

            when (result) {
                is Result.Error -> {
                    _state.update { it.copy(isLoggingIn = false) }
                    eventChannel.send(LoginEvent.LoginError(result.error))
                }

                is Result.Success -> {
                    analyticsRepository.setUser()
                    crashlyticsRepository.setUser(email)
                    _state.update { it.copy(isLoggingIn = false) }
                    eventChannel.send(LoginEvent.LoginSuccess)
                }
            }
        }
    }

}