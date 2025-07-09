package com.done.partner.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.core.presentation.core.util.toMap
import com.done.partner.domain.repositories.auth.AuthRepository
import com.done.partner.domain.repositories.settings.SettingsRepository
import com.done.partner.domain.repositories.store.StoreRepository
import com.done.core.presentation.core.util.UiAction
import com.done.core.domain.repositories.analytics.AnalyticsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val storeRepository: StoreRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    var state by mutableStateOf(SettingsState())
        private set

    private val eventChannel = Channel<SettingsEvent>()
    val event = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            state = state.copy(
                currentLanguage = settingsRepository.getCurrentLanguage(),
                languages = settingsRepository.getAllLanguages(),
                receiveNotifications = settingsRepository.receivingNotifications(),
                storeId = storeRepository.getStoreId(),
                printLangCode = settingsRepository.getPrintLang()
            )
        }
    }

    fun onAction(action: SettingsActions) {
        viewModelScope.launch {
            analyticsRepository.logEvent(UiAction.SettingsActions, action.toMap())
        }
        when (action) {
            SettingsActions.OnToggleReceiveNotifications -> {
                state = state.copy(receiveNotifications = !state.receiveNotifications)
                viewModelScope.launch {
                    settingsRepository.toggleReceiveNotifications(state.receiveNotifications)
                }
            }

            is SettingsActions.OnChangeLanguage -> {
                viewModelScope.launch {
                    settingsRepository.changeLanguage(state.storeId, action.code)
                    state = state.copy(
                        currentLanguage = settingsRepository.getCurrentLanguage(),
                        languages = settingsRepository.getAllLanguages()
                    )
                    eventChannel.send(SettingsEvent.LanguageChanged)
                }
            }

            SettingsActions.OnLogout -> {
                viewModelScope.launch {
                    authRepository.logout(isFromCTA = true)
                    eventChannel.send(SettingsEvent.LoggedOut)
                }
            }

            is SettingsActions.OnSelectPrinterType -> {
                viewModelScope.launch {
                    state = state.copy(printerType = action.printerType)
                    settingsRepository.setPrinterType(action.printerType)
                }
            }

            is SettingsActions.OnTestPrinter -> {
                viewModelScope.launch {
                    settingsRepository.testPrinter(action.ticket)
                }
            }

            is SettingsActions.OnChangePrintLang -> {
                viewModelScope.launch {
                    state = state.copy(printLangCode = action.langCode)
                    settingsRepository.setPrintLang(action.langCode)
                }
            }

            SettingsActions.OnHelpClick -> {
                // TODO Handle help click
            }
        }
    }

}