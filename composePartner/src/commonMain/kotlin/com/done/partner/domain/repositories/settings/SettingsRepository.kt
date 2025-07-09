package com.done.partner.domain.repositories.settings

import com.done.core.domain.models.language.Language
import com.done.partner.domain.util.PrinterType

interface SettingsRepository {

    fun registerDeviceToken()

    suspend fun testPrinter(ticket: ByteArray)

    suspend fun getPrintLang(): String

    suspend fun setPrintLang(langCode: String)

    suspend fun setPrinterType(printerType: PrinterType)

    suspend fun toggleReceiveNotifications(receive: Boolean)

    suspend fun receivingNotifications(): Boolean

    suspend fun changeLanguage(storeId: String?, code: String)

    fun getCurrentLanguage(): Language

    fun getAllLanguages(): List<Language>

    companion object {
        const val KEY_RECEIVE_NOTIFICATIONS = "KEY_SHOW_NOTIFICATION"
    }
}