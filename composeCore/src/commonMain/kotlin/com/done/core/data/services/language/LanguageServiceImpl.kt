package com.done.core.data.services.language

import com.done.core.domain.services.language.LanguageService

expect class LanguageServiceImpl : LanguageService {
    override fun changeLanguage(code: String)
    override fun getCurrentLanguage(): String
}
