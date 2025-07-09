package com.done.core.domain.models.error_response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val message: String? = null)