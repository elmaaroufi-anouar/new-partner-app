package com.done.partner.presentation.login

import com.done.core.domain.util.result.NetworkError

sealed interface LoginEvent {
    data object RestartApp: LoginEvent
    data object LoginSuccess: LoginEvent
    data class LoginError(val error: NetworkError?): LoginEvent
}