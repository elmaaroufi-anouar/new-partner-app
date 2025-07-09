package com.done.partner.presentation.settings

import com.done.core.presentation.core.util.Action
import com.done.partner.domain.util.PrinterType

sealed interface SettingsActions : Action {
    data class OnSelectPrinterType(val printerType: PrinterType) : SettingsActions
    data class OnChangePrintLang(val langCode: String) : SettingsActions
    data class OnTestPrinter(val ticket: ByteArray) : SettingsActions
    data object OnHelpClick : SettingsActions
    data object OnLogout : SettingsActions
    data object OnToggleReceiveNotifications : SettingsActions
    data class OnChangeLanguage(val code: String) : SettingsActions
}