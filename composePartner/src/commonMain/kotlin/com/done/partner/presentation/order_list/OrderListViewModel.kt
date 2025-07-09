package com.done.partner.presentation.order_list

import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.core.domain.repositories.analytics.AnalyticsRepository
import com.done.core.domain.repositories.remote_config.RemoteConfigRepository
import com.done.core.domain.util.result.Result
import com.done.core.presentation.core.util.UiAction
import com.done.core.presentation.core.util.toMap
import com.done.partner.BuildConfig
import com.done.partner.domain.models.orders.status.MarkOrderAsStatus
import com.done.partner.domain.models.orders.status.OrderStatus
import com.done.partner.domain.repositories.auth.AuthRepository
import com.done.partner.domain.repositories.order.OrderRepository
import com.done.partner.domain.repositories.play_services.PlayServicesRepository
import com.done.partner.domain.repositories.store.StoreRepository
import com.done.partner.domain.util.OrderNotificationsSender
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class OrderListViewModel(
    private val storeRepository: StoreRepository,
    private val orderRepository: OrderRepository,
    private val orderNotificationsSender: OrderNotificationsSender,
    private val authRepository: AuthRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val playServicesRepository: PlayServicesRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val versionCode: Int
        get() = BuildConfig.VERSION_CODE

    private val _state = MutableStateFlow(OrderListState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<OrderListEvent>()
    val event = eventChannel.receiveAsFlow()

    private var fetchingOrdersJob: Job? = null

    init {
        viewModelScope.launch {
            orderNotificationsSender.notificationsFlow.collect { notification ->
                getAllOrders()
            }
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedStoreId = storeRepository.getStoreId(),
                    printLangCode = orderRepository.getPrintLang()
                )
            }
        }

        initConfig()
    }

    fun onAction(action: OrderListAction) {
        viewModelScope.launch {
            analyticsRepository.logEvent(UiAction.OrdersAction, action.toMap())
        }
        when (action) {
            is OrderListAction.OnLoad -> {
                load()
            }

            OrderListAction.OnPullToRefresh -> {
                load()
            }

            OrderListAction.OnStopFetchingOrderList -> {
                stopFetchingOrders()
            }

            OrderListAction.OnToggleStoreAvailabilityDialog -> {
                _state.update {
                    it.copy(
                        isToggleStoreAvailabilityDialogShowing = !it.isToggleStoreAvailabilityDialogShowing
                    )
                }
            }

            OrderListAction.OnToggleStoreAvailability -> {
                changeStoreAvailability()
            }

            is OrderListAction.OnPrintOrder -> {
                viewModelScope.launch {
                    orderRepository.printOrder(action.ticket, printTwo = true)
                }
            }

            is OrderListAction.OnAcceptOrder -> {
                updateOrderStatus(
                    markOrderAsStatus = MarkOrderAsStatus.BEING_PREPARED, orderId = action.order.id
                )
            }

            is OrderListAction.OnMarkOrderAsReady -> {
                updateOrderStatus(
                    markOrderAsStatus = MarkOrderAsStatus.READY_FOR_PICKUP, orderId = action.orderId
                )
            }

            is OrderListAction.OnConfirmDelivery -> {
                state.value.selectedOrderToConfirmId?.let {
                    updateOrderStatus(
                        markOrderAsStatus = MarkOrderAsStatus.DELIVERED, orderId = it
                    )
                }
            }

            is OrderListAction.OnToggleDeliveryCodeInputDialog -> {
                _state.update {
                    it.copy(
                        isDeliveryCodeInputDialogShowing = !it.isDeliveryCodeInputDialogShowing,
                        selectedOrderToConfirmId = action.orderId
                    )
                }
                state.value.deliveryCodeTextState.clearText()
            }

            is OrderListAction.OnPaginateTab1 -> {
                val tabOnePagination = state.value.firstTabPagination
                if (tabOnePagination != null && tabOnePagination.totalPages > tabOnePagination.currentPage) {
                    _state.update {
                        it.copy(
                            firstTabPagination = tabOnePagination.copy(
                                currentPage = tabOnePagination.currentPage + 1
                            )
                        )
                    }
                    getFirstTabOrders(isPaginate = true)
                }
            }

            is OrderListAction.OnPaginateTab2 -> {
                val tabTwoPagination = state.value.secondTabPagination
                if (tabTwoPagination != null && tabTwoPagination.totalPages > tabTwoPagination.currentPage) {
                    _state.update {
                        it.copy(
                            secondTabPagination = tabTwoPagination.copy(
                                currentPage = tabTwoPagination.currentPage + 1
                            )
                        )
                    }

                    getSecondTabOrders(isPaginate = true)
                }
            }

            is OrderListAction.OnToggleRequestUpdatePlayServicesDialog -> {
                _state.update {
                    it.copy(
                        isRequestUpdatePlayServicesDialogShowing = !it.isRequestUpdatePlayServicesDialogShowing
                    )
                }
                if (action.updatePlayServices) {
                    checkPlayServices(doUpdate = true)
                }
            }

            is OrderListAction.OnOrderClick -> {
                // handled by UI
            }

            OrderListAction.OnUpdateVersionClick -> {
                // handled by UI
            }
        }
    }

    private fun checkPlayServices(doUpdate: Boolean) {
        viewModelScope.launch {
            if (doUpdate) {
                updatePlayServices()
            } else {
                _state.update {
                    it.copy(
                        isRequestUpdatePlayServicesDialogShowing = true
                    )
                }
            }
        }
    }

    private suspend fun updatePlayServices() {
        if (state.value.isUpdatingPlayServices || playServicesRepository.isPlayServicesUpdated()) {
            return
        }
        val url = remoteConfigRepository.getConfig()?.partnerAndroidPlayServicesUrl ?: state.value.playServicesUrl
        _state.update {
            it.copy(
                isUpdatingPlayServices = true
            )
        }
        playServicesRepository.updatePlayServices(
            url = url,
            onPackageInstalled = {
                viewModelScope.launch {
                    println("XapkInstaller install finished, hasToke: ${playServicesRepository.isPlayServicesUpdated()}")
                    eventChannel.send(OrderListEvent.RestartApp)
                }
            }
        )
    }

    private fun load() {
        viewModelScope.launch {
            val isPlayServicesUpdated = playServicesRepository.isPlayServicesUpdated()
            if (state.value.activateFCM == null) {
                val activateFCM = remoteConfigRepository.getConfig()?.partnerAndroidActivateFCM
                println("XapkInstaller activateFCM: ${!isPlayServicesUpdated && state.value.activateFCM == true}")

                _state.update {
                    it.copy(
                        activateFCM = activateFCM
                    )
                }
                if (!isPlayServicesUpdated && state.value.activateFCM == true) {
                    checkPlayServices(doUpdate = false)
                } else {
                    getStoreAndOrders()
                }
            } else {
                if (!isPlayServicesUpdated && state.value.activateFCM == true) {
                    checkPlayServices(doUpdate = false)
                } else {
                    getStoreAndOrders()
                }
            }
        }
    }

    private fun getStoreAndOrders() {
        getStores(getOrdersToo = state.value.selectedStoreId == null)
        if (state.value.selectedStoreId != null) {
            getAllOrders()
        }
        startFetchingOrders()
    }

    private fun updateOrderStatus(markOrderAsStatus: String, orderId: String) {
        viewModelScope.launch {
            _state.update {
                when (markOrderAsStatus) {
                    MarkOrderAsStatus.CANCELLED -> {
                        it.copy(orderThatIsBeingMarkedRefusedId = orderId)
                    }

                    MarkOrderAsStatus.DELIVERED -> {
                        it.copy(isConfirmingDelivery = true)
                    }

                    else -> {
                        it.copy(orderThatIsBeingMarkedAcceptedOrReadyId = orderId)
                    }
                }
            }

            state.value.selectedStoreId?.let { storeId ->
                val result = orderRepository.updateOrderStatus(
                    order = state.value.firstTabOrders.firstOrNull { it.id == orderId },
                    storeId = storeId,
                    orderId = orderId,
                    markOrderAsStatus = markOrderAsStatus,
                    customerFriendlyCode = state.value.deliveryCodeTextState.text.toString()
                )

                if (result is Result.Success) {
                    _state.update {
                        it.copy(
                            orderThatIsBeingMarkedAcceptedOrReadyId = "",
                            orderThatIsBeingMarkedRefusedId = "",
                            isConfirmingDelivery = false,
                            isDeliveryCodeInputDialogShowing = false,
                            selectedOrderToConfirmId = null
                        )
                    }
                    state.value.deliveryCodeTextState.clearText()
                    updateLocalOrderStatus(
                        markOrderAsStatus = markOrderAsStatus,
                        orderId = orderId
                    )
                } else {
                    _state.update {
                        it.copy(
                            orderThatIsBeingMarkedAcceptedOrReadyId = "",
                            orderThatIsBeingMarkedRefusedId = "",
                            isConfirmingDelivery = false
                        )
                    }
                    eventChannel.send(OrderListEvent.Error(result.error))
                }
            }
        }
    }

    private fun updateLocalOrderStatus(markOrderAsStatus: String, orderId: String) {
        when (markOrderAsStatus) {
            MarkOrderAsStatus.BEING_PREPARED -> acceptLocalOrder(orderId = orderId)
            MarkOrderAsStatus.READY_FOR_PICKUP -> readyLocalOrder(orderId = orderId)
            else -> deliverLocalOrder(orderId = orderId) // status is MarkOrderAsStatus.DELIVERED
        }
    }

    private fun acceptLocalOrder(orderId: String) {
        _state.update {
            it.copy(
                firstTabOrders = it.firstTabOrders.map { order ->
                    if (order.id == orderId) {
                        order.copy(status = OrderStatus.BEING_PREPARED)
                    } else {
                        order
                    }
                },
                secondTabOrders = it.firstTabOrders.map { order ->
                    if (order.id == orderId) {
                        order.copy(status = OrderStatus.BEING_PREPARED)
                    } else {
                        order
                    }
                }
            )
        }
    }

    private fun cancelLocalOrder(orderId: String) {
        state.value.firstTabOrders.forEach { order ->
            if (order.id == orderId) {
                _state.update {
                    it.copy(
                        firstTabOrders = it.firstTabOrders - order,
                        secondTabOrders = it.secondTabOrders + order.copy(status = OrderStatus.CANCELLED)
                    )
                }
                return@forEach
            }
        }
    }

    private fun readyLocalOrder(orderId: String) {
        _state.update {
            it.copy(
                firstTabOrders = it.firstTabOrders.map { order ->
                    if (order.id == orderId) {
                        order.copy(status = OrderStatus.READY_FOR_PICKUP)
                    } else {
                        order
                    }
                }
            )
        }
    }

    private fun deliverLocalOrder(orderId: String) {
        _state.update {
            it.copy(
                firstTabOrders = it.firstTabOrders.map { order ->
                    if (order.id == orderId) {
                        order.copy(status = OrderStatus.DELIVERED)
                    } else {
                        order
                    }
                }
            )
        }
    }

    private fun getAllOrders() {
        getFirstTabOrders()
        getSecondTabOrders()
    }

    private fun getFirstTabOrders(isPaginate: Boolean = false) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoadingFirstTabOrders = true
                )
            }
            if (!isPaginate) {
                _state.update {
                    it.copy(
                        firstTabPagination = it.firstTabPagination?.copy(currentPage = 1)
                    )
                }
            }

            state.value.selectedStoreId?.let { storeId ->
                val result = orderRepository.getOrders(
                    storeId = storeId,
                    status = listOf(
                        OrderStatus.PENDING,
                        OrderStatus.BEING_PREPARED,
                        OrderStatus.READY_FOR_PICKUP
                    ),
                    page = state.value.firstTabPagination?.currentPage ?: 1
                )

                if (result is Result.Success) {
                    val list = result.data ?: emptyList()
                    _state.update {
                        it.copy(
                            firstTabOrders = if (isPaginate) it.firstTabOrders + list else list,
                            firstTabPagination = result.pagination
                        )
                    }
                } else {
                    eventChannel.send(OrderListEvent.Error(result.error))
                }
            }

            _state.update {
                it.copy(
                    isLoadingFirstTabOrders = false
                )
            }
        }
    }

    private fun getSecondTabOrders(isPaginate: Boolean = false) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoadingSecondTabOrders = true
                )
            }
            if (!isPaginate) {
                _state.update {
                    it.copy(
                        secondTabPagination = it.secondTabPagination?.copy(currentPage = 1)
                    )
                }
            }

            state.value.selectedStoreId?.let { storeId ->
                val result = orderRepository.getOrders(
                    storeId = storeId,
                    status = listOf(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
                    page = state.value.secondTabPagination?.currentPage ?: 1
                )

                if (result is Result.Success) {
                    val list = result.data ?: emptyList()
                    _state.update {
                        it.copy(
                            secondTabOrders = if (isPaginate) it.secondTabOrders + list else list,
                            secondTabPagination = result.pagination
                        )
                    }
                } else {
                    eventChannel.send(OrderListEvent.Error(result.error))
                }
            }

            _state.update {
                it.copy(
                    isLoadingSecondTabOrders = false
                )
            }
        }
    }

    private fun getStores(getOrdersToo: Boolean = false) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoadingStore = true
                )
            }

            val result = storeRepository.getStores()

            if (result is Result.Success) {
                _state.update {
                    it.copy(
                        stores = result.data ?: emptyList(),
                        selectedStore = result.data?.firstOrNull(),
                        isStoreOpen = result.data?.firstOrNull()?.disabledAt == null
                    )
                }
                result.data?.firstOrNull()?.id?.let { id ->
                    _state.update {
                        it.copy(
                            selectedStoreId = id
                        )
                    }
                    if (getOrdersToo) {
                        getAllOrders()
                    }
                }

                if (!state.value.isStoreOpen && state.value.isFirstTimeLoadingStore) {
                    _state.update {
                        it.copy(
                            isFirstTimeLoadingStore = false,
                            isToggleStoreAvailabilityDialogShowing = true
                        )
                    }
                }

            } else {
                if (result.error?.code == 401) {
                    authRepository.logout(isFromCTA = false)
                    eventChannel.send(OrderListEvent.Unauthorized)
                } else {
                    eventChannel.send(OrderListEvent.Error(result.error))
                }
            }
            _state.update {
                it.copy(
                    isLoadingStore = false
                )
            }
        }
    }

    private fun changeStoreAvailability() {
        _state.update {
            it.copy(
                isToggleStoreAvailabilityDialogShowing = false
            )
        }

        state.value.selectedStoreId?.let { storeId ->
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isChangingStoreAvailability = true
                    )
                }

                val result = if (state.value.isStoreOpen) {
                    storeRepository.closeStore(storeId)
                } else {
                    storeRepository.openStore(storeId)
                }

                if (result is Result.Success) {
                    _state.update {
                        it.copy(
                            isStoreOpen = !it.isStoreOpen
                        )
                    }
                } else {
                    eventChannel.send(OrderListEvent.Error(result.error))
                }

                _state.update {
                    it.copy(
                        isChangingStoreAvailability = false
                    )
                }
            }
        }
    }

    private fun initConfig() {
        viewModelScope.launch {
            val forceUpdate = shouldForceUpdate(remoteConfigRepository)
            _state.update {
                it.copy(
                    shouldForceUpdate = forceUpdate
                )
            }
        }
    }

    private suspend fun shouldForceUpdate(remoteConfigRepository: RemoteConfigRepository): Boolean {
        val minRequired = remoteConfigRepository.getConfig()?.partnerAndroidVersion ?: return false
        remoteConfigRepository.getConfig()?.partnerAndroidUpdateUrl?.let { partnerAndroidUpdateUrl ->
            _state.update {
                it.copy(
                    updateUrl = partnerAndroidUpdateUrl
                )
            }
        }
        return versionCode < minRequired
    }

    private fun startFetchingOrders() {
        fetchingOrdersJob?.cancel()
        fetchingOrdersJob = null
        fetchingOrdersJob = viewModelScope.launch {
            while (isActive) {
                delay(60.seconds.inWholeMilliseconds)
                if (state.value.selectedStoreId != null) {
                    getAllOrders()
                }
            }
        }
    }

    private fun stopFetchingOrders() {
        fetchingOrdersJob?.cancel()
        fetchingOrdersJob = null
    }

    override fun onCleared() {
        super.onCleared()
        fetchingOrdersJob?.cancel()
        fetchingOrdersJob = null
    }
}