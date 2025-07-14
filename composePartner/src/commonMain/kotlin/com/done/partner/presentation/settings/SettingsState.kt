package com.done.partner.presentation.settings

import com.done.partner.domain.util.Printer
import com.done.partner.domain.util.PrinterType
import com.done.core.domain.models.language.Language
import com.done.core.domain.models.language.LanguageCodes

data class SettingsState(
    val storeId: String? = null,
    val receiveNotifications: Boolean = true,
    val printLangCode: String? = null,
    val printerType: PrinterType? = Printer.CURRENT_PRINTER_TYPE,
    val languages: List<Language> = listOf(),
    val currentLanguage: Language = Language(
        nameResource = Res.string.french,
        code = LanguageCodes.FR,
        image = Res.drawable.frensh
    )
)
