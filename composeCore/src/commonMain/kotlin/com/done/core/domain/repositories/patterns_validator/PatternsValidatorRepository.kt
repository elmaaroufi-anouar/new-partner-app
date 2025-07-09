package com.done.core.domain.repositories.patterns_validator

interface PatternsValidatorRepository {
    fun matches(value: String): Boolean
}