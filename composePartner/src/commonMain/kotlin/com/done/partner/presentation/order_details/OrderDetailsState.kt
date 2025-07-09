package com.done.partner.presentation.order_details

import androidx.compose.foundation.text.input.TextFieldState
import com.done.partner.domain.models.orders.Order

data class OrderDetailsState(
    val isLoading: Boolean = true,
    val order: Order? = null,
    val storeName: String? = null,
    val printLangCode: String? = null,
    val storeIdToRefresh: String? = null,
    val orderIdToRefresh: String? = null,
    val statusToRefresh: String? = null,
    val isConfirmingDelivery: Boolean = false,
    val isDeliveryCodeInputDialogShowing: Boolean = false,
    val deliveryCodeTextState: TextFieldState = TextFieldState(),
)