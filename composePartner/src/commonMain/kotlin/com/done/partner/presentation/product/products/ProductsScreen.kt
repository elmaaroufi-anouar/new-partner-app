package com.done.partner.presentation.product.products

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.done.partner.domain.models.product.Product
import com.done.core.presentation.core.design_system.*
import com.done.core.presentation.core.ui.components.ObserveAsEvent
import com.done.core.presentation.core.ui.components.networkErrorToast
import com.done.core.presentation.core.ui.theme.DoneTheme
import com.done.core.presentation.core.ui.theme.doneBackgroundOrange
import com.done.core.presentation.core.ui.theme.doneGreen
import com.done.partner.presentation.core.components.ToggleStoreAvailabilityButton
import com.done.partner.presentation.core.components.ToggleStoreAvailabilityDialog
import com.done.partner.presentation.product.products.components.DisableProductsFab
import com.done.partner.presentation.product.products.components.UpdateProductPriceDialog
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductsScreenCore(
    viewModel: ProductsViewModel = koinViewModel(),
    onProductClick: (String) -> Unit
) {

    // reloading store & products if they don't.json exist
    // every time the user navigates to the products screen
    LaunchedEffect(true) {
        viewModel.onAction(ProductsAction.OnReload)
    }

    val context = LocalContext.current

    ObserveAsEvent(viewModel.event) { event ->
        when (event) {
            is ProductsEvent.Error -> {
                networkErrorToast(
                    networkError = event.networkError,
                    context = context,
                )
            }
        }
    }

    ProductsScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                is ProductsAction.OnProductClick -> {
                    if (action.hasOptions) {
                        onProductClick(action.productId)
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.this_product_has_no_options), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsScreen(
    state: ProductsState,
    onAction: (ProductsAction) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    DoneScaffold(
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
            state = rememberTopAppBarState()
        ),
        topBar = { topAppBarScrollBehavior ->
            DoneTopBar(
                scrollBehavior = topAppBarScrollBehavior,
                titleText = stringResource(Res.string.products),
                actionIconContent = {
                    ToggleStoreAvailabilityButton(
                        storeExists = state.selectedStore != null,
                        isStoreOpen = state.isStoreOpen,
                        onToggleStoreAvailabilityDialog = {
                            onAction(ProductsAction.OnToggleStoreAvailabilityDialog)
                        },
                        isLoading = state.isLoadingStore || state.isChangingStoreAvailability
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        onPullToRefresh = {
            onAction(ProductsAction.OnPullToRefresh)
        }
    ) { padding ->

        Column(
            modifier = Modifier.padding(top = padding.calculateTopPadding())
        ) {

            Spacer(Modifier.height(8.dp))
            DoneTextField(
                textFieldState = state.searchProductsQueryState,
                hint = stringResource(Res.string.search_in_products),
                startIcon = Icons.Rounded.Search,
                startIconTint = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Box {
                if (state.isLoadingProducts) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (state.products.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(Res.string.you_have_no_products))
                    }
                } else {
                    Products(
                        state = state,
                        onAction = onAction,
                    )
                }
            }
        }

        if (state.isDisableProductsFabShowing) {
            DisableProductsFab(
                onClick = {
                    onAction(ProductsAction.OnAcceptDisableSelectedProductsClick)
                }
            )
        }

        if (state.isUndoDisableProductsSnackbarShowing) {
            DoneActionSnackbar(
                snackbarHostState = snackbarHostState,
                onUndo = {
                    onAction(ProductsAction.OnUndoDisableSelectedProducts)
                },
                message = stringResource(Res.string.product_successfully_disabled),
                actionLabel = stringResource(Res.string.undo)
            )
        }

        if (state.isUpdateProductPriceDialogShowing) {
            UpdateProductPriceDialog(
                state = state,
                onDismiss = {
                    onAction(ProductsAction.OnToggleUpdateProductPriceDialog(null))
                },
                onSave = {
                    onAction(ProductsAction.OnUpdateProductPrice)
                }
            )
        }

        if (state.isToggleStoreAvailabilityDialogShowing) {
            ToggleStoreAvailabilityDialog(
                isStoreOpen = state.isStoreOpen,
                onToggleStoreAvailability = {
                    onAction(ProductsAction.OnToggleStoreAvailability)
                },
                onDismiss = {
                    onAction(ProductsAction.OnToggleStoreAvailabilityDialog)
                }
            )
        }
    }
}

@Composable
private fun Products(
    modifier: Modifier = Modifier,
    state: ProductsState,
    onAction: (ProductsAction) -> Unit
) {

    val listState = rememberLazyListState()

    val shouldPaginate = remember {
        derivedStateOf {
            val itemCount = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex == itemCount - 1 && !state.isLoadingProducts
        }
    }

    LaunchedEffect(key1 = listState) {
        snapshotFlow { shouldPaginate.value }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                onAction(ProductsAction.OnPaginate)
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 22.dp, bottom = 16.dp)
    ) {
        itemsIndexed(state.products) { index, product ->
            ProductItem(
                index = index,
                product = product,
                state = state,
                onAction = onAction
            )
            HorizontalDivider(Modifier.alpha(0.3f))
        }
    }
}

@Composable
private fun ProductItem(
    modifier: Modifier = Modifier,
    index: Int,
    product: Product,
    state: ProductsState,
    onAction: (ProductsAction) -> Unit
) {
    Row(
        modifier = modifier
            .background(
                if (product.isEnabled) {
                    Color.Transparent
                } else {
                    doneBackgroundOrange
                }
            )
            .clickable {
                onAction(
                    ProductsAction.OnProductClick(product.id, product.optionGroups.isNotEmpty())
                )
            }
            .padding(vertical = 12.dp)
            .padding(start = 4.dp, end = 12.dp)
    ) {
        Box(
            modifier = Modifier.size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isUpdatingProductsAvailability && product.isSelectedToDisable) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Checkbox(
                    checked = product.isSelectedToDisable && !state.isUndoDisableProductsSnackbarShowing,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            onAction(ProductsAction.OnSelectProductToDisable(index))
                        } else {
                            onAction(ProductsAction.OnUnselectProductToDisable(index))
                        }
                    },
                    enabled = product.isEnabled,
                    modifier = Modifier.alpha(if (product.isEnabled) 1.0f else 0.1f)
                )
            }
        }

        Spacer(Modifier.width(4.dp))

        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .size(70.dp)
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.2f),
                    shape = RoundedCornerShape(10.dp)
                )
                .clip(RoundedCornerShape(10.dp))
                .alpha(
                    if (product.isEnabled) {
                        1.0f
                    } else {
                        0.7f
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (product.assets.isNotEmpty()) {
                AsyncImage(
                    model = product.assets[0].imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(Res.drawable.product_dark),
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(40.dp)
                        .alpha(0.3f)
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .weight(1f)
        ) {
            Column {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(Modifier.height(8.dp))

                TextButton(
                    modifier = Modifier,
                    onClick = {
                        onAction(ProductsAction.OnToggleUpdateProductPriceDialog(index))
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(0.9f)
                    )
                ) {
                    Text(
                        text = product.price?.display ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null
                    )
                }

                Spacer(Modifier.height(8.dp))
            }

            if (!product.isEnabled) {
                DoneOutlinedButton(
                    text = stringResource(Res.string.enable),
                    textColor = doneGreen,
                    style = MaterialTheme.typography.bodyLarge,
                    isLoading = state.selectedProductToEnableIndex == index || state.isUpdatingProductsAvailability && product.isSelectedToDisable,
                    borderColor = doneGreen,
                    onClick = {
                        onAction(ProductsAction.OnEnableProduct(index))
                    }
                )
                Spacer(Modifier.height(6.dp))
            }
        }

        if (product.optionGroups.isNotEmpty()) {
            IconButton(
                onClick = {
                    onAction(
                        ProductsAction.OnProductClick(product.id, product.optionGroups.isNotEmpty())
                    )
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProductsScreenPreview() {
    DoneTheme {
        ProductsScreen(
            state = ProductsState()
        )
    }
}