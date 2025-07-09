package com.done.core.data.services.language

import android.app.LocaleManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.LocaleList
import com.done.core.domain.models.language.LanguageCodes
import java.util.Locale
import androidx.core.content.edit
import com.done.core.domain.services.language.LanguageService

expect class LanguageServiceImpl : LanguageService {
    override fun changeLanguage(code: String)
    override fun getCurrentLanguage(): String
}
