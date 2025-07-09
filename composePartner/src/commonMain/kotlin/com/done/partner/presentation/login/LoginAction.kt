package com.done.partner.presentation.login

import com.done.core.presentation.core.util.Action

sealed interface LoginAction: Action {
    data object OnLoad: LoginAction
    data object OnTogglePasswordVisibilityClick: LoginAction
    data class OnCheckCanLoginTest(
        val email: String, val password: String
    ) : LoginAction
    data object OnLoginClick: LoginAction
}