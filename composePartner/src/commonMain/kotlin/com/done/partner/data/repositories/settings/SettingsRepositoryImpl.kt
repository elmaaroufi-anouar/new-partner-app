package com.done.partner.data.repositories.settings

import com.done.core.data.util.ApiRoutes
import com.done.core.data.services.api.KtorApiService
import com.done.core.domain.services.language.LanguageService
import com.done.core.domain.services.notifications.DeviceTokenRegisteringService
import com.done.core.domain.services.secure_storage.SecureStorageService
import com.done.core.domain.models.language.Language
import com.done.core.domain.models.language.LanguageCodes
import com.done.partner.domain.services.print.PrinterService
import com.done.partner.domain.repositories.settings.SettingsRepository
import com.done.partner.domain.util.PrinterType

class SettingsRepositoryImpl(
    private val languageService: LanguageService,
    private val secureStorageService: SecureStorageService,
    private val printerService: PrinterService,
    private val apiService: KtorApiService,
    private val deviceTokenRegisteringService: DeviceTokenRegisteringService
) : SettingsRepository {

    override fun registerDeviceToken() {
        deviceTokenRegisteringService.registerDeviceToken()
    }

    override suspend fun testPrinter(ticket: ByteArray) {
        printerService.printOrder(ticket, true)
    }

    override suspend fun getPrintLang(): String {
        return printerService.getPrintLang()
    }

    override suspend fun setPrintLang(langCode: String) {
        printerService.setPrintLang(langCode)
    }

    override suspend fun setPrinterType(printerType: PrinterType) {
        return printerService.setPrinterType(printerType)
    }

    override suspend fun toggleReceiveNotifications(receive: Boolean) {
        secureStorageService.putBoolean(SettingsRepository.KEY_RECEIVE_NOTIFICATIONS, receive)
    }

    override suspend fun receivingNotifications(): Boolean {
        return secureStorageService.getBoolean(SettingsRepository.KEY_RECEIVE_NOTIFICATIONS, true)
    }

    override suspend fun changeLanguage(storeId: String?, code: String) {
        languageService.changeLanguage(code)
        if (storeId != null) {
            apiService.patch<Unit>(
                route = ApiRoutes.changeLanguage(storeId),
                body = hashMapOf("default_lang" to code)
            )
        }
    }

    override fun getCurrentLanguage(): Language {
        val currentLanguageCode = languageService.getCurrentLanguage()

        return getAllLanguages().firstOrNull {
            it.code == currentLanguageCode
        } ?: Language(
            nameResource = Res.string.french,
            code = LanguageCodes.FR,
            image = Res.drawable.frensh
        )
    }

    override fun getAllLanguages(): List<Language> {
        val languages = listOf(
            Language(
                nameResource = Res.string.english,
                code = LanguageCodes.EN,
                image = Res.drawable.english
            ),
            Language(
                nameResource = Res.string.arabic,
                code = LanguageCodes.AR,
                image = Res.drawable.arabic
            ),
            Language(
                nameResource = Res.string.french,
                code = LanguageCodes.FR,
                image = Res.drawable.frensh
            )
        )

        return languages
    }
}