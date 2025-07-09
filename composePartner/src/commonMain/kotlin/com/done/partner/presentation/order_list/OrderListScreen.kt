package com.done.partner.presentation.order_list

import android.graphics.Picture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.done.partner.domain.models.orders.Order
import com.done.partner.domain.models.orders.status.OrderStatus
import com.done.core.presentation.core.design_system.DoneButton
import com.done.core.presentation.core.design_system.DoneDialog
import com.done.core.presentation.core.design_system.DoneOutlinedButton
import com.done.core.presentation.core.design_system.DoneScaffold
import com.done.core.presentation.core.design_system.DoneTopBar
import com.done.core.presentation.core.ui.components.AnimatedVisibilityItem
import com.done.core.presentation.core.ui.components.ObserveAsEvent
import com.done.core.presentation.core.ui.components.OnPauseCompose
import com.done.core.presentation.core.ui.components.OnResumeCompose
import com.done.core.presentation.core.ui.components.networkErrorToast
import com.done.core.presentation.core.ui.theme.DoneTheme
import com.done.core.presentation.core.ui.theme.doneBackgroundOrange
import com.done.partner.R
import com.done.partner.presentation.core.components.DeliveryCodeDialog
import com.done.partner.presentation.core.components.ScreenShootTicket
import com.done.partner.presentation.core.components.ToggleStoreAvailabilityButton
import com.done.partner.presentation.core.components.ToggleStoreAvailabilityDialog
import com.done.partner.presentation.core.components.UpdatingPlayServicesDialog
import com.done.partner.presentation.core.components.createBitmapBytesFromPicture
import com.done.core.presentation.core.util.openFirebaseDistribution
import com.done.partner.presentation.order_list.components.BeingPreparedOrders
import com.done.partner.presentation.order_list.components.DeliverdAndCancelledOrders
import com.done.partner.presentation.order_list.components.PendingOrders
import com.done.partner.presentation.order_list.components.ReadyToPickupOrders
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel

@Composable
fun OrderListScreenCore(
    viewModel: OrderListViewModel = koinViewModel(),
    onOrderDetails: (orderId: String, orderStatus: String) -> Unit,
    onRestartApp: (Boolean) -> Unit
) {

    OnResumeCompose {
        viewModel.onAction(OrderListAction.OnLoad)
    }

    OnPauseCompose {
        viewModel.onAction(OrderListAction.OnStopFetchingOrderList)
    }

    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvent(viewModel.event) { event ->
        when (event) {
            is OrderListEvent.Error -> {
                networkErrorToast(
                    networkError = event.networkError,
                    context = context
                )
            }

            OrderListEvent.Unauthorized -> {
                onRestartApp(false)
            }

            OrderListEvent.RestartApp -> {
                onRestartApp(true)
            }
        }
    }

    val orderToPrint = remember { mutableStateOf<Order?>(null) }
    val scope = rememberCoroutineScope()
    var picture = remember { Picture() }
    var isCapturing by remember { mutableStateOf(false) }
    if (isCapturing && orderToPrint.value != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ScreenShootTicket(
                order = orderToPrint.value!!,
                printLang = state.printLangCode,
                storeName = state.selectedStore?.storeBrand?.name,
                picture = picture,
                printTwo = true
            )
        }
    }

    OrderListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is OrderListAction.OnOrderClick -> {
                    onOrderDetails(action.orderId, action.orderStatus)
                }

                is OrderListAction.OnUpdateVersionClick -> {
                    openFirebaseDistribution(context, state.updateUrl)
                }

                is OrderListAction.OnAcceptOrder -> {
                    scope.launch {
                        orderToPrint.value = action.order
                        isCapturing = true
                        try {
                            delay(500)
                            viewModel.onAction(OrderListAction.OnPrintOrder(createBitmapBytesFromPicture(picture)))
                        } catch (_: Exception) {
                            try {
                                delay(500)
                                viewModel.onAction(OrderListAction.OnPrintOrder(createBitmapBytesFromPicture(picture)))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        isCapturing = false
                        orderToPrint.value = null
                        picture = Picture()
                    }
                    viewModel.onAction(action)
                }

                else -> viewModel.onAction(action)
            }
        }
    )

    if (state.isUpdatingPlayServices) {
        UpdatingPlayServicesDialog()
    }

    if (state.isRequestUpdatePlayServicesDialogShowing && !state.isUpdatingPlayServices) {
        DoneDialog(
            title = stringResource(R.string.update_play_services),
            description = stringResource(R.string.play_services_desc),
            image = painterResource(R.drawable.google_play),
            imageWidth = 100.dp,
            betweenButtonsPadding = 8.dp,
            primaryButton = {
                DoneButton(
                    text = stringResource(R.string.update),
                    onClick = {
                        viewModel.onAction(
                            OrderListAction.OnToggleRequestUpdatePlayServicesDialog(
                                updatePlayServices = true
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            },
            secondaryButton = {
                DoneOutlinedButton(
                    text = stringResource(R.string.cancel),
                    onClick = {
                        viewModel.onAction(
                            OrderListAction.OnToggleRequestUpdatePlayServicesDialog(
                                updatePlayServices = false
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderListScreen(
    state: OrderListState,
    onAction: (OrderListAction) -> Unit
) {
    DoneScaffold(
        containerColor = doneBackgroundOrange,
        showTopBarHorizontalDivider = false,
        topBar = { topAppBarScrollBehavior ->
            DoneTopBar(
                scrollBehavior = topAppBarScrollBehavior,
                titleText = state.selectedStore?.storeBrand?.name,
                actionIconContent = {
                    ToggleStoreAvailabilityButton(
                        storeExists = state.selectedStore != null,
                        isStoreOpen = state.isStoreOpen,
                        isLoading = state.isLoadingStore || state.isChangingStoreAvailability,
                        onToggleStoreAvailabilityDialog = {
                            onAction(OrderListAction.OnToggleStoreAvailabilityDialog)
                        }
                    )
                }
            )
        },
        onPullToRefresh = {
            onAction(OrderListAction.OnPullToRefresh)
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
        ) {
            Orders(
                state = state,
                onAction = onAction
            )

            AnimatedVisibilityItem(
                isVisible = state.isLoadingStore || state.isLoadingFirstTabOrders || state.isLoadingSecondTabOrders
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 65.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                }
            }
        }

        if (state.isToggleStoreAvailabilityDialogShowing) {
            ToggleStoreAvailabilityDialog(
                isStoreOpen = state.isStoreOpen,
                onToggleStoreAvailability = {
                    onAction(OrderListAction.OnToggleStoreAvailability)
                },
                onDismiss = {
                    onAction(OrderListAction.OnToggleStoreAvailabilityDialog)
                }
            )
        }

    }

    if (state.shouldForceUpdate) {
        DoneDialog(
            modifier = Modifier,
            title = stringResource(R.string.new_version_available),
            description = stringResource(R.string.message_new_version_available),
            primaryButton = {
                DoneButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    onClick = { onAction(OrderListAction.OnUpdateVersionClick) },
                    text = stringResource(R.string.update),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        )
    }

    if (state.isDeliveryCodeInputDialogShowing) {
        DeliveryCodeDialog(
            textFieldState = state.deliveryCodeTextState,
            isLoading = state.isConfirmingDelivery,
            onDismiss = {
                onAction(OrderListAction.OnToggleDeliveryCodeInputDialog(null))
            },
            onConfirm = {
                onAction(OrderListAction.OnConfirmDelivery)
            }
        )
    }
}

@Composable
fun Orders(
    modifier: Modifier = Modifier,
    state: OrderListState,
    onAction: (OrderListAction) -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    TabRow(
        modifier = modifier.height(53.dp),
        containerColor = MaterialTheme.colorScheme.onPrimary,
        selectedTabIndex = selectedTabIndex.value,
    ) {
        repeat(2) { index ->
            Tab(
                selected = selectedTabIndex.value == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
                    Text(
                        text = if (index == 0) {
                            stringResource(R.string.ongoing)
                        } else {
                            stringResource(R.string.finished)
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (selectedTabIndex.value == index) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        }
                    )
                }
            )
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp)
    ) {

        if (selectedTabIndex.value == 0) {
            val listState = rememberLazyListState()
            PaginatedLazyColumn(
                listState = listState,
                isLoading = state.isLoadingFirstTabOrders,
                onPaginate = { onAction(OrderListAction.OnPaginateTab1) },
                content = {
                    PendingOrders(
                        orders = state.firstTabOrders.filter { it.status == OrderStatus.PENDING },
                        state = state,
                        onAction = onAction
                    )

                    BeingPreparedOrders(
                        orders = state.firstTabOrders.filter { it.status == OrderStatus.BEING_PREPARED },
                        state = state,
                        onAction = onAction
                    )

                    ReadyToPickupOrders(
                        orders = state.firstTabOrders.filter { it.status == OrderStatus.READY_FOR_PICKUP },
                        onAction = onAction
                    )
                }
            )
        } else {
            val listState = rememberLazyListState()
            PaginatedLazyColumn(
                listState = listState,
                isLoading = state.isLoadingSecondTabOrders,
                onPaginate = { onAction(OrderListAction.OnPaginateTab2) },
                content = {
                    DeliverdAndCancelledOrders(
                        state = state,
                        orders = state.secondTabOrders,
                        onAction = onAction
                    )
                }
            )
        }
    }
}

@Composable
fun PaginatedLazyColumn(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    isLoading: Boolean,
    onPaginate: () -> Unit,
    content: LazyListScope.() -> Unit
) {
    val shouldPaginate = remember {
        derivedStateOf {
            val itemCount = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleIndex == itemCount - 1 && !isLoading
        }
    }

    LaunchedEffect(key1 = listState) {
        snapshotFlow { shouldPaginate.value }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                onPaginate()
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 22.dp),
        content = content
    )
}


@Preview
@Composable
private fun OrdersScreenPreview() {
    DoneTheme {
        OrderListScreen(
            state = OrderListState(),
            onAction = {}
        )
    }
}