package com.done.partner.presentation.order_details

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Picture
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.done.partner.domain.models.customer.Customer
import com.done.partner.domain.models.driver.Driver
import com.done.partner.domain.models.orders.Order
import com.done.partner.domain.models.orders.OrderItem
import com.done.partner.domain.models.orders.OrderItemOption
import com.done.partner.domain.models.orders.previewOrders
import com.done.partner.domain.models.orders.status.OrderStatus
import com.done.core.domain.util.result.NetworkErrorName
import com.done.core.presentation.core.design_system.DoneButton
import com.done.core.presentation.core.design_system.DoneDialog
import com.done.core.presentation.core.design_system.DoneLargeTopBar
import com.done.core.presentation.core.design_system.DoneOutlinedButton
import com.done.core.presentation.core.design_system.DoneScaffold
import com.done.core.presentation.core.ui.components.ObserveAsEvent
import com.done.core.presentation.core.ui.components.OnResumeCompose
import com.done.core.presentation.core.ui.components.networkErrorToast
import com.done.core.presentation.core.ui.theme.DoneTheme
import com.done.core.presentation.core.ui.theme.doneGreen
import com.done.core.presentation.core.ui.theme.doneOrange
import com.done.partner.R
import com.done.partner.presentation.core.components.DeliveryCodeDialog
import com.done.partner.presentation.core.components.ScreenShootTicket
import com.done.partner.presentation.core.components.createBitmapBytesFromPicture
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OrderDetailsScreenCore(
    viewModel: OrderDetailsViewModel = koinViewModel(),
    orderId: String,
    orderStatus: String,
    onGoBack: () -> Unit,
    onEditOrder: (orderId: String) -> Unit
) {

    BackHandler {
        onGoBack()
    }

    OnResumeCompose {
        viewModel.onAction(
            OrderDetailsAction.OnLoad(
                orderId = orderId, status = orderStatus
            )
        )
    }

    val context = LocalContext.current
    var showNoInternetDialog by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvent(viewModel.event) { event ->
        when (event) {
            is OrderDetailsEvent.OrderStatusUpdated -> onGoBack()

            is OrderDetailsEvent.Error -> {
                networkErrorToast(
                    networkError = event.networkError,
                    context = context,
                )

                if (event.networkError?.name == NetworkErrorName.NO_INTERNET_ERROR) {
                    showNoInternetDialog = true
                }
            }
        }
    }

    if (showNoInternetDialog) {
        DoneDialog(
            image = painterResource(R.drawable.image_error_internet),
            description = stringResource(com.done.core.R.string.make_sure_you_have_a_valid_internet_connection),
            title = stringResource(R.string.no_internet_connection),
            onDismiss = { showNoInternetDialog = false },
            betweenButtonsPadding = 10.dp,
            secondaryButton = {
                DoneOutlinedButton(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.bodyLarge,
                    verticalPadding = 4.dp,
                    onClick = { showNoInternetDialog = false },
                    modifier = Modifier.weight(1f)
                )
            },
            primaryButton = {
                DoneButton(
                    text = stringResource(R.string.activate),
                    style = MaterialTheme.typography.bodyLarge,
                    verticalPadding = 4.dp,
                    onClick = {
                        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        )
    }

    val picture = remember { Picture() }
    var isCapturing by remember { mutableStateOf(false) }
    var printTwo by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    state.order?.let {
        if (isCapturing) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                ScreenShootTicket(
                    order = it,
                    printLang = state.printLangCode,
                    storeName = state.storeName,
                    picture = picture,
                    printTwo = printTwo
                )
            }
        }
    }

    OrderDetailsScreen(
        state = state,
        status = orderStatus,
        onAction = { action ->
            when (action) {
                OrderDetailsAction.OnGoBack -> onGoBack()
                OrderDetailsAction.OnEditOrder -> {
                    state.order?.id?.let { onEditOrder(it) }
                }

                is OrderDetailsAction.OnStartPrintOrder -> {
                    scope.launch {
                        isCapturing = true
                        printTwo = action.printTwo
                        try {
                            delay(500)
                            viewModel.onAction(OrderDetailsAction.OnPrintOrder(createBitmapBytesFromPicture(picture), printTwo))
                        } catch (_: Exception) {
                            try {
                                delay(500)
                                viewModel.onAction(OrderDetailsAction.OnPrintOrder(createBitmapBytesFromPicture(picture), printTwo))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        isCapturing = false
                    }
                }

                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetailsScreen(
    state: OrderDetailsState,
    status: String,
    onAction: (OrderDetailsAction) -> Unit
) {

    val order = state.order
    val orderItems = order?.orderItems

    var isDeclineOrderDialogShowing by remember { mutableStateOf(false) }

    DoneScaffold(
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        showTopBarHorizontalDivider = true,
        topBar = { scrollBehavior ->
            DoneLargeTopBar(
                scrollBehavior = scrollBehavior,
                navigationIconContent = {
                    val text: String
                    when (status) {
                        OrderStatus.PENDING -> {
                            text = stringResource(R.string.pending)
                        }

                        OrderStatus.BEING_PREPARED -> {
                            text = stringResource(R.string.being_prepared)
                        }

                        OrderStatus.READY_FOR_PICKUP -> {
                            text = stringResource(R.string.ready)
                        }

                        OrderStatus.DELIVERED -> {
                            text = stringResource(R.string.delivered)
                        }

                        else -> {
                            text = stringResource(R.string.declined)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                onAction(OrderDetailsAction.OnGoBack)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = null
                            )
                        }

                        Text(
                            text = text,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
                        )
                    }
                },
                actionIconContent = {
                    if (
                        state.order?.status == OrderStatus.PENDING || state.order?.status == OrderStatus.BEING_PREPARED
                    ) {
                        DoneOutlinedButton(
                            verticalPadding = 0.dp,
                            horizontalPadding = 0.dp,
                            onClick = {
                                onAction(OrderDetailsAction.OnEditOrder)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.edit),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                },
                titleContent = {
                    order?.let { OrderDetailsNumber(order) }
                }
            )
        },
        onPullToRefresh = {
            onAction(OrderDetailsAction.OnRefresh)
        }
    ) { padding ->
        if (!state.isLoading && state.order == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.couldn_t_load_order),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(16.dp))

                DoneOutlinedButton(
                    onClick = { onAction(OrderDetailsAction.OnRefresh) },
                    text = stringResource(R.string.try_again),
                    style = MaterialTheme.typography.bodyMedium,
                    verticalPadding = 1.dp,
                    textColor = doneGreen,
                    borderColor = doneGreen,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    Modifier
                        .padding(top = 24.dp)
                        .align(Alignment.TopCenter)
                        .size(25.dp)
                )
            }
            if (state.order != null) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 150.dp),
                ) {

                    if (!state.order.inStorePickup && state.order.driver != null) {
                        OrderDetailsDriver(driver = state.order.driver)
                        HorizontalDivider(Modifier.alpha(0.2f))
                    }

                    if (state.order.customer != null) {
                        OrderDetailsCustomer(customer = state.order.customer)
                    }

                    Spacer(Modifier.height(16.dp))

                    if (
                        state.order.minutesUntilPreparation != null
                        && state.order.status == OrderStatus.PENDING
                        || state.order.status == OrderStatus.BEING_PREPARED
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = stringResource(R.string.preparation_time),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${state.order.minutesUntilPreparation} ${stringResource(R.string.minutes)}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    orderItems?.forEachIndexed { index, orderDetailsItem ->
                        OrderDetailsItems(
                            orderItem = orderDetailsItem
                        )
                    }

                    HorizontalDivider()

                    if (state.order.note.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.message),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                text = stringResource(R.string.customer_request),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = state.order.note,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(Modifier.height(16.dp))

                        HorizontalDivider()
                    }

                    Spacer(Modifier.height(16.dp))

                    TotalPrices(order = state.order)

                    Spacer(Modifier.height(20.dp))

                    if (order.inStorePickup && state.order.status == OrderStatus.READY_FOR_PICKUP) {
                        DoneButton(
                            text = stringResource(R.string.confirm_delivery),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(65.dp)
                                .padding(horizontal = 16.dp),
                            buttonColor = MaterialTheme.colorScheme.primary,
                            verticalPadding = 4.dp,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            onClick = {
                                onAction(OrderDetailsAction.OnToggleDeliveryCodeInputDialog)
                            },
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    if (status != OrderStatus.CANCELLED) {
                        DoneOutlinedButton(
                            onClick = {
                                onAction(OrderDetailsAction.OnStartPrintOrder(printTwo = false))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Print,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = stringResource(R.string.print_order),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(50.dp))
                }
            }

            BottomButton(
                state = state,
                order = order,
                status = status,
                onAction = onAction,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    if (state.isDeliveryCodeInputDialogShowing && order != null) {
        DeliveryCodeDialog(
            textFieldState = state.deliveryCodeTextState,
            isLoading = state.isConfirmingDelivery,
            onDismiss = {
                onAction(OrderDetailsAction.OnToggleDeliveryCodeInputDialog)
            },
            onConfirm = {
                onAction(OrderDetailsAction.OnConfirmDelivery(order.id))
            }
        )
    }

    if (isDeclineOrderDialogShowing) {
        DoneDialog(
            title = stringResource(R.string.decline_order),
            description = stringResource(R.string.are_you_sure_you_want_to_decline_this_order),
            onDismiss = {
                isDeclineOrderDialogShowing = false
            },
            betweenButtonsPadding = 16.dp,
            primaryButton = {
                DoneButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        isDeclineOrderDialogShowing = false
                    },
                    text = stringResource(R.string.no)
                )
            },
            secondaryButton = {
                DoneOutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onAction(OrderDetailsAction.OnDeclineOrder)
                        isDeclineOrderDialogShowing = false
                    },
                    text = stringResource(R.string.yes)
                )
            }
        )
    }
}

@Composable
fun BottomButton(
    modifier: Modifier = Modifier,
    state: OrderDetailsState,
    order: Order?,
    status: String,
    onAction: (OrderDetailsAction) -> Unit
) {
    if (status == OrderStatus.PENDING || status == OrderStatus.BEING_PREPARED) {
        order?.let {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 16.dp)
            ) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onBackground.copy(0.08f)
                )

                Spacer(Modifier.height(16.dp))

                DoneButton(
                    onClick = {
                        if (order.status == OrderStatus.PENDING) {
                            onAction(OrderDetailsAction.OnStartPrintOrder(printTwo = true))
                            onAction(OrderDetailsAction.OnAcceptOrder)
                        } else {
                            onAction(OrderDetailsAction.OnMarkOrderAsReadyForPickup)
                        }
                    },
                    text = if (status == OrderStatus.PENDING) {
                        stringResource(R.string.accept_order)
                    } else {
                        stringResource(R.string.mark_as_ready)
                    },
                    isLoading = state.isLoading,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    buttonColor = if (status == OrderStatus.PENDING) {
                        doneGreen
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun OrderDetailsNumber(order: Order) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onPrimary)
            .fillMaxWidth()
            .padding(vertical = 20.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${order.friendlyNumber}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.width(14.dp))

            if (order.inStorePickup) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.DirectionsWalk,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .size(34.dp)
                        .background(
                            doneOrange.copy(0.2f),
                            RoundedCornerShape(50)
                        )
                        .padding(4.dp),
                )
            }
        }

        val date = if (order.createdAt.length > 4) {
            order.createdAt.take(10)
        } else if (order.createdAt.isNotEmpty()) {
            stringResource(R.string.min_ago, order.createdAt)
        } else {
            ""
        }
        Text(
            text = date,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )
    }
}


@Composable
fun OrderDetailsCustomer(
    customer: Customer
) {
    ElevatedCard(
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onPrimary)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.size(34.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                if (customer.firstName.isNotBlank() || customer.lastName.isNotBlank()) {
                    Text(
                        text = "${customer.firstName} ${customer.lastName}",
                        lineHeight = 1.sp,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                if (customer.email.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = customer.email
                    )
                }

                if (customer.phone.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = customer.phone
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TotalPrices(
    order: Order
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Text(
            text = stringResource(R.string.total_price),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        Text(
            text = order.productAmount?.display ?: "",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }

    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Text(
            text = stringResource(R.string.vat_incl),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )

        Text(
            text = String.format("%.2f", order.vat),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
        )
    }
}

@Composable
fun OrderDetailsDriver(
    driver: Driver
) {
    if (driver.phone.isNotBlank() || driver.firstName.isNotBlank() || driver.lastName.isNotBlank()) {
        ElevatedCard(
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.driver),
                    lineHeight = 1.sp,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = driver.profileImageUrl,
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onBackground.copy(0.1f))
                    )

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "${driver.firstName} ${
                                if (driver.lastName.take(1)
                                        .isNotEmpty()
                                ) driver.lastName.take(1) + "" else ""
                            }",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = driver.phone,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderDetailsItems(
    orderItem: OrderItem
) {
    OrderDetailsItem(orderItem = orderItem)

    Spacer(Modifier.height(10.dp))

    val orderItemOptionsData = orderItem.orderItemOptions
    if (orderItemOptionsData.isNotEmpty()) {
        orderItemOptionsData.forEachIndexed { _, orderDetailsItemOptions ->
            OrderDetailsItemOptionContent(
                orderItemOption = orderDetailsItemOptions
            )
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(22.dp))

    }
}

@Composable
fun OrderDetailsItem(
    modifier: Modifier = Modifier,
    orderItem: OrderItem
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "${orderItem.quantity}x",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = orderItem.name,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.width(16.dp))

        Text(
            text = orderItem.price?.display ?: "",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun OrderDetailsItemOptionContent(
    modifier: Modifier = Modifier,
    orderItemOption: OrderItemOption,
) {
    Row(
        modifier = modifier
            .padding(start = 57.dp, end = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "${orderItemOption.quantity}x",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(30.dp)
            )

            Text(
                text = orderItemOption.name,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(Modifier.width(10.dp))

        Text(
            text = orderItemOption.price?.display ?: "",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )

    }
}

@Preview
@Composable
private fun OrderDetailsScreenPreview() {
    DoneTheme {
        OrderDetailsScreen(
            state = OrderDetailsState(
                order = previewOrders[0]
            ),
            status = OrderStatus.PENDING,
            onAction = {}
        )
    }
}