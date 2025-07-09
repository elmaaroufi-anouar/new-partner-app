package com.done.partner.presentation.login

import androidx.compose.foundation.text.input.TextFieldState

data class LoginState(
    val email: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val canLogin: Boolean = false,
    val isLoggingIn: Boolean = false,

    val activateFCM: Boolean? = null,
    val isUpdatingPlayServices: Boolean = false,
    val playServicesUrl: String = "https://assets.done.ma/GooglePlayServices.xapk"
)
