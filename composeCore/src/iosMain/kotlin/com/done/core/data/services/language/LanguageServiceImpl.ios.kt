package com.done.core.data.services.language

import com.done.core.domain.services.language.LanguageService

actual class LanguageServiceImpl : LanguageService {
    actual override fun changeLanguage(code: String) {
    }

    actual override fun getCurrentLanguage(): String {
        TODO("Not yet implemented")
    }
}