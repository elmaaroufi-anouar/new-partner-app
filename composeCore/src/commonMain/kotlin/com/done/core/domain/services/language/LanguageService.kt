package com.done.core.domain.services.language

interface LanguageService {

    fun changeLanguage(code: String)

    fun getCurrentLanguage(): String
}