package com.done.partner.presentation.core.components

import androidx.compose.runtime.Composable
import com.done.partner.domain.models.orders.Order

@Composable
actual fun ScreenShootTicket(
    order: Order,
    printLang: String?,
    storeName: String?,
    printTwo: Boolean,
    onPictureReady: (ByteArray) -> Unit
) {
}