package com.done.app

import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    isLoggedIn: Boolean = false,
    orderId: String?,
    orderStatus: String?,
) {
    Navigation(
        isLoggedIn = isLoggedIn,
        orderId = orderId,
        orderStatus = orderStatus
    )
}
