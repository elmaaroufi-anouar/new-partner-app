package com.done.core.data.repositories.patterns_validator

import com.done.core.domain.repositories.patterns_validator.PatternsValidatorRepository

class PatternsValidatorRepositoryImpl : PatternsValidatorRepository {
    private val emailRegex = Regex(
        "^([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+)\\.([a-zA-Z]{2,})$"
    )
    override fun matches(value: String): Boolean {
        return emailRegex.matches(value)
    }
}