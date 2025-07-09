package com.done.partner.presentation.settings

sealed interface SettingsEvent {
    data object LanguageChanged: SettingsEvent
    data object LoggedOut: SettingsEvent
}