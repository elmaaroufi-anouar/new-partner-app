package com.done.partner.presentation.order_details

import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.core.domain.repositories.analytics.AnalyticsRepository
import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.NetworkErrorName
import com.done.core.domain.util.result.Result
import com.done.core.domain.util.result.UNPROCESSABLE_ENTITY_CODE
import com.done.core.presentation.core.util.UiAction
import com.done.core.presentation.core.util.toMap
import com.done.partner.domain.models.orders.status.MarkOrderAsStatus
import com.done.partner.domain.models.orders.status.OrderStatus
import com.done.partner.domain.repositories.order.OrderRepository
import com.done.partner.domain.repositories.store.StoreRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderDetailsViewModel(
    private val orderRepository: OrderRepository,
    private val storeRepository: StoreRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailsState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<OrderDetailsEvent>()
    val event = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    storeIdToRefresh = storeRepository.getStoreId(),
                    printLangCode = orderRepository.getPrintLang()
                )
            }
        }
    }

    fun onAction(action: OrderDetailsAction) {
        viewModelScope.launch {
            analyticsRepository.logEvent(UiAction.OrderDetailsAction, action.toMap())
        }
        when (action) {
            is OrderDetailsAction.OnLoad -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            orderIdToRefresh = action.orderId,
                            statusToRefresh = action.status,
                            storeName = storeRepository.getStoreName()
                        )
                    }
                    val storeId = state.value.storeIdToRefresh ?: storeRepository.getStoreId()
                    if (storeId != null) {
                        loadOrderDetails(
                            storeId = storeId,
                            orderId = action.orderId,
                        )
                    }
                }
            }

            OrderDetailsAction.OnRefresh -> {
                refreshOrder()
            }

            OrderDetailsAction.OnDeclineOrder -> {
                updateOrderStatus(MarkOrderAsStatus.CANCELLED, OrderStatus.CANCELLED)
            }

            OrderDetailsAction.OnAcceptOrder -> {
                updateOrderStatus(MarkOrderAsStatus.BEING_PREPARED, OrderStatus.BEING_PREPARED)
            }

            OrderDetailsAction.OnMarkOrderAsReadyForPickup -> {
                updateOrderStatus(MarkOrderAsStatus.READY_FOR_PICKUP, OrderStatus.READY_FOR_PICKUP)
            }

            is OrderDetailsAction.OnConfirmDelivery -> {
                updateOrderStatus(MarkOrderAsStatus.DELIVERED, OrderStatus.DELIVERED)
            }

            OrderDetailsAction.OnToggleDeliveryCodeInputDialog -> {
                _state.update {
                    it.copy(
                        isDeliveryCodeInputDialogShowing = !it.isDeliveryCodeInputDialogShowing
                    )
                }
                state.value.deliveryCodeTextState.clearText()
            }

            is OrderDetailsAction.OnPrintOrder -> {
                viewModelScope.launch {
                    orderRepository.printOrder(action.ticket, action.printTwo)
                }
            }

            OrderDetailsAction.OnGoBack -> {} // Handled by UI
            OrderDetailsAction.OnEditOrder -> {} // Handled by UI
            is OrderDetailsAction.OnStartPrintOrder -> {} // Handled by UI
        }
    }

    private fun refreshOrder(withLoading: Boolean = true) {
        viewModelScope.launch {
            if (withLoading) {
                _state.update {
                    it.copy(
                        isLoading = true
                    )
                }
            }
            val storeId = state.value.storeIdToRefresh ?: storeRepository.getStoreId()
            val orderId = state.value.orderIdToRefresh

            if (storeId != null && orderId != null) {
                loadOrderDetails(
                    storeId = storeId, orderId = orderId
                )
            } else {
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun loadOrderDetails(
        storeId: String, orderId: String
    ) {
        _state.update { it.copy(isLoading = true) }
        val result = orderRepository.getOrderDetails(storeId, orderId)
        _state.update { it.copy(isLoading = false) }

        if (result is Result.Success) {
            _state.update {
                it.copy(
                    order = result.data
                )
            }
        } else {
            eventChannel.send(OrderDetailsEvent.Error(result.error))
        }
    }

    private fun updateOrderStatus(
        markOrderAsStatus: String,
        orderStatus: String
    ) {
        val storeId = state.value.order?.storeId
        val orderId = state.value.order?.id

        if (storeId == null || orderId == null) {
            viewModelScope.launch {
                eventChannel.send(
                    OrderDetailsEvent.Error(NetworkError(NetworkErrorName.UNKNOWN))
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            if (orderStatus == OrderStatus.DELIVERED) {
                _state.update {
                    it.copy(
                        isConfirmingDelivery = true
                    )
                }
            }
            val result = orderRepository.updateOrderStatus(
                order = state.value.order,
                storeId = storeId,
                orderId = orderId,
                markOrderAsStatus = markOrderAsStatus,
                customerFriendlyCode = state.value.deliveryCodeTextState.text.toString()
            )
            _state.update {
                it.copy(
                    isLoading = false, isConfirmingDelivery = false
                )
            }
            if (result is Result.Success) {
                _state.update {
                    it.copy(
                        order = it.order?.copy(status = orderStatus)
                    )
                }
                eventChannel.send(OrderDetailsEvent.OrderStatusUpdated)
            } else {
                if (result.error?.code == UNPROCESSABLE_ENTITY_CODE) {
                    refreshOrder(withLoading = false)
                }
                eventChannel.send(OrderDetailsEvent.Error(result.error))
            }
        }
    }
}