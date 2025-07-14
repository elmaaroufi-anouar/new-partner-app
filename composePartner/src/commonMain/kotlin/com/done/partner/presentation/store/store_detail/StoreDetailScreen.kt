package com.done.partner.presentation.store.store_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.done.core.presentation.core.design_system.DoneButton
import com.done.core.presentation.core.design_system.DoneScaffold
import com.done.core.presentation.core.design_system.DoneTopBar
import com.done.core.presentation.core.ui.components.ObserveAsEvent
import com.done.core.presentation.core.ui.components.OnResumeCompose
import com.done.core.presentation.core.ui.components.networkErrorToast
import com.done.partner.presentation.product.products.showToast
import com.done.partner.presentation.store.product_detail.ProductDetailSheetRoot
import com.done.partner.presentation.store.store_detail.component.ProductInlineItem
import com.done.partner.presentation.store.store_detail.component.ShadowDivider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StoreDetailScreenRoot(
    viewModel: StoreDetailViewModel = koinViewModel(),
    orderId: String,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    ObserveAsEvent(viewModel.event) { event ->
        when (event) {
            is StoreDetailEvent.Error -> {
                networkErrorToast(networkError = event.networkError)
            }
            StoreDetailEvent.OrderUpdated -> {
                showToast(message = Res.string.order_updated)
                onBackClick()
            }

            is StoreDetailEvent.ScrollToFirstSelectedProduct -> {
                scope.launch {
                    listState.animateScrollToItem((event.section + event.product) + 3)
                }
            }
        }
    }

    OnResumeCompose {
        viewModel.onAction(StoreDetailAction.OnLoad(orderId))
    }

    StoreDetailScreen(
        state = state,
        orderId = orderId,
        listState = listState,
        onAction = { action ->
            when (action) {
                StoreDetailAction.OnBackClick -> onBackClick()
                else -> viewModel.onAction(action)
            }
        }
    )

    state.selectedProduct?.let { selectedProduct ->
        ProductDetailSheetRoot(
            product = selectedProduct,
            cartProduct = state.selectedCartProduct,
            isProductSheetShown = state.isProductSheetShown,
            onBack = { viewModel.onAction(StoreDetailAction.OnCloseProductSheet) },
            onIncrementQuantity = { product, productOptions, quantity ->
                viewModel.onAction(
                    StoreDetailAction.OnIncrementQuantity(product, productOptions, quantity)
                )
            },
            onDecrementQuantity = { product, productOptions, removeAll ->
                viewModel.onAction(
                    StoreDetailAction.OnDecrementQuantity(product, productOptions, removeAll)
                )
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    state: StoreDetailState,
    orderId: String,
    listState: LazyListState,
    onAction: (StoreDetailAction) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { index ->
            if (index - 2 >= 0) {
                selectedTabIndex = index - 2
            }
        }
    }

    DoneScaffold(
        withScrollBehavior = false,
        containerColor = MaterialTheme.colorScheme.background,
        showTopBarHorizontalDivider = false,
        topBar = {
            Column {
                DoneTopBar(
                    navigationIcon = Icons.AutoMirrored.Default.ArrowBackIos,
                    onNavigationClick = { onAction(StoreDetailAction.OnBackClick) },
                    titleText = state.store?.storeBrand?.name ?: "",
                    containerColor = MaterialTheme.colorScheme.background,
                )
                if (state.store != null) {
                    TabRow(
                        state = state,
                        selectedTabIndex = selectedTabIndex,
                        onSelectedTabIndexChanged = { index ->
                            selectedTabIndex = index
                        },
                        coroutineScope = coroutineScope,
                        listState = listState
                    )
                }
            }
        },
        onPullToRefresh = {
            onAction(StoreDetailAction.OnPullToRefresh(orderId))
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(
                visible = state.isLoading || state.isRefreshing,
                enter = fadeIn(), exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            AnimatedVisibility(
                visible = !state.isLoading && !state.isRefreshing && state.store != null,
                enter = fadeIn(), exit = fadeOut(),
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        bottom = if ((state.cart?.cartProducts?.size ?: 0) > 0) 120.dp else 0.dp
                    )
                ) {

                    item {
                        if (state.isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                CircularProgressIndicator(Modifier.align(Alignment.Center))
                            }
                        }

                        if (state.store == null && !state.isLoading) {
                            Box(Modifier.fillMaxSize()) {
                                Text(
                                    text = stringResource(Res.string.something_went_wrong),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Normal
                                    ),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(top = 50.dp)
                                )
                            }
                        }
                    }

                    if (!state.isLoading && state.store?.storeSections?.isNotEmpty() == false) {
                        item {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = stringResource(Res.string.no_products_found),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Normal
                                    ),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                                    modifier = Modifier
                                        .padding(20.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // products by section
                    state.store?.storeSections?.let { sections ->
                        items(sections.size) { sectionIndex ->
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = sections[sectionIndex].name,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                textAlign = TextAlign.Start,
                                maxLines = 2,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            )

                            sections[sectionIndex].products.forEachIndexed { index, product ->
                                val cartProducts = state.cart?.cartProducts?.filter { it.product.id == product.id }

                                ProductInlineItem(
                                    product = product,
                                    cartProducts = cartProducts,
                                    onAction = onAction,
                                    modifier = Modifier
                                        .clickable {
                                            onAction(
                                                StoreDetailAction.OnOpenProductSheet(
                                                    product = product, cartProduct = null
                                                )
                                            )
                                        }
                                        .padding(horizontal = 20.dp)
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(
                                        if (index == sections[sectionIndex].products.lastIndex) 0.2f else 0.07f
                                    )
                                )
                            }
                        }
                    }
                }
            }

            if (state.countProducts > 0) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(0.08f))
                    DoneButton(
                        text = stringResource(Res.string.confirm),
                        isLoading = state.isUpdatingOrder,
                        onClick = { onAction(StoreDetailAction.OnConfirmOrder) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TabRow(
    state: StoreDetailState,
    selectedTabIndex: Int,
    onSelectedTabIndexChanged: (Int) -> Unit,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 0.dp,
        containerColor = MaterialTheme.colorScheme.background,
        divider = {
            ShadowDivider(
                elevation = 8.dp,
                color = MaterialTheme.colorScheme.background.copy(0.03f)
            )
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        state.store?.storeSections?.forEachIndexed { index, section ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = {
                    onSelectedTabIndexChanged(index)
                    coroutineScope.launch {
                        listState.animateScrollToItem(index + 2)
                    }
                },
                text = {
                    Text(
                        text = section.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (index == selectedTabIndex) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            )
        }
    }
}