package com.done.core.data.services.language

import android.content.Context
import com.done.core.domain.models.language.LanguageCodes
import com.done.core.domain.services.language.LanguageService
import android.app.LocaleManager
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.LocaleList
import java.util.Locale
import androidx.core.content.edit

actual class LanguageServiceImpl(
    private val context: Context
) : LanguageService {
    private val prefs = context.getSharedPreferences("done_partner_language_prefs", MODE_PRIVATE)

    fun setCurrentLanguageToBaseContext(): Context? {
        val code = getCurrentLanguage()

        prefs.edit { putString(KEY_LANGUAGE_CODE, code) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                .applicationLocales = LocaleList.forLanguageTags(code)
        }

        val locale = Locale(code)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    actual override fun changeLanguage(code: String) {
        prefs.edit { putString(KEY_LANGUAGE_CODE, code) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                .applicationLocales = LocaleList.forLanguageTags(code)
        }

        val locale = Locale(code)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        context.createConfigurationContext(configuration)
    }

    actual override fun getCurrentLanguage(): String {
        return prefs.getString(KEY_LANGUAGE_CODE, LanguageCodes.DEF) ?: LanguageCodes.DEF
    }

    companion object Companion {
        private const val KEY_LANGUAGE_CODE = "KEY_LANGUAGE_CODE"
    }
}