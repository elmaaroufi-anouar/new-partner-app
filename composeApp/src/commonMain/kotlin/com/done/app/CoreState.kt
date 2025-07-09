package com.done.app

data class CoreState(
    val isLoggedIn: Boolean? = null,
    val isCheckingLogIn: Boolean = true,
    val activateFCM: Boolean? = null
)