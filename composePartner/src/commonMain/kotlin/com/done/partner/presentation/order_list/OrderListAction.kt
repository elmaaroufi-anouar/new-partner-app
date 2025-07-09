package com.done.partner.presentation.order_list

import com.done.partner.domain.models.orders.Order
import com.done.core.presentation.core.util.Action

sealed interface OrderListAction : Action {
    data object OnToggleStoreAvailabilityDialog : OrderListAction
    data object OnToggleStoreAvailability : OrderListAction
    data object OnPullToRefresh : OrderListAction
    data object OnLoad : OrderListAction
    data object OnStopFetchingOrderList : OrderListAction
    data object OnPaginateTab1 : OrderListAction
    data object OnPaginateTab2 : OrderListAction
    data class OnOrderClick(val orderId: String, val orderStatus: String) : OrderListAction
    data class OnMarkOrderAsReady(val orderId: String) : OrderListAction
    data class OnPrintOrder(val ticket: ByteArray) : OrderListAction
    data class OnToggleRequestUpdatePlayServicesDialog(
        val updatePlayServices: Boolean
    ) : OrderListAction
    data class OnAcceptOrder(val order: Order) : OrderListAction

    data object OnUpdateVersionClick : OrderListAction
    data object OnConfirmDelivery : OrderListAction
    data class OnToggleDeliveryCodeInputDialog(val orderId: String? = null) : OrderListAction
}