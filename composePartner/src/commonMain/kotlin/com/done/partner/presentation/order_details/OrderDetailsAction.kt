package com.done.partner.presentation.order_details

import com.done.core.presentation.core.util.Action

sealed interface OrderDetailsAction: Action {
    data class OnLoad(
        val orderId: String, val status: String
    ) : OrderDetailsAction
    data object OnRefresh : OrderDetailsAction
    data object OnAcceptOrder : OrderDetailsAction
    data object OnMarkOrderAsReadyForPickup : OrderDetailsAction
    data object OnDeclineOrder : OrderDetailsAction
    data object OnGoBack : OrderDetailsAction
    data class OnStartPrintOrder(val printTwo: Boolean) : OrderDetailsAction
    data class OnPrintOrder(val ticket: ByteArray, val printTwo: Boolean) : OrderDetailsAction
    data object OnEditOrder : OrderDetailsAction
    data class OnConfirmDelivery(val orderId: String) : OrderDetailsAction
    data object OnToggleDeliveryCodeInputDialog : OrderDetailsAction
}