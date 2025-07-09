package com.done.partner.presentation.store.product_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.done.partner.domain.models.cart.CartProduct
import com.done.partner.domain.models.cart.CartProductOption
import com.done.partner.domain.models.product.Product
import com.done.core.presentation.core.design_system.DoneButton
import com.done.core.presentation.core.design_system.DoneScaffold
import com.done.partner.R
import com.done.partner.presentation.store.product_detail.component.OptionGroupItem
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductDetailSheetRoot(
    viewModel: ProductDetailViewModel = koinViewModel(),
    product: Product,
    cartProduct: CartProduct?,
    isProductSheetShown: Boolean,
    onBack: () -> Unit,
    onIncrementQuantity: (
        product: Product, productOptions: List<CartProductOption>, quantity: Int
    ) -> Unit,
    onDecrementQuantity: (
        product: Product, productOptions: List<CartProductOption>, removeAll: Boolean
    ) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(true) {
        viewModel.onAction(
            ProductDetailAction.OnFirstLoad(product = product, cartProduct = cartProduct)
        )
    }

    val snackBarHostState = remember { SnackbarHostState() }

    ProductDetailSheet(
        state = state,
        isProductSheetShown = isProductSheetShown,
        onAction = { action ->
            when (action) {
                ProductDetailAction.OnBack -> {
                    onBack()
                }

                is ProductDetailAction.OnIncrementQuantity -> {
                    onIncrementQuantity(
                        action.product, action.productOptions, action.quantity
                    )
                }

                is ProductDetailAction.OnDecrementQuantity -> {
                    onDecrementQuantity(
                        action.product, action.productOptions, action.removeAll
                    )
                }

                else -> viewModel.onAction(action)
            }
        },
        snackBarHostState = snackBarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailSheet(
    modifier: Modifier = Modifier,
    state: ProductDetailState,
    isProductSheetShown: Boolean,
    onAction: (ProductDetailAction) -> Unit,
    snackBarHostState: SnackbarHostState,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val stretchFactor by remember { mutableFloatStateOf(1f) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        listState.animateScrollToItem(0)
    }

    val isRowVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 100
        }
    }

    if (isProductSheetShown) {
        ModalBottomSheet(
            modifier = modifier
                .fillMaxHeight()
                .padding(top = 50.dp),
            onDismissRequest = {
                scope.launch { sheetState.hide() }
                onAction(ProductDetailAction.OnBack)
            },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = null,
        ) {
            DoneScaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackBarHostState)
                },
                withScrollBehavior = false,
                showTopBarHorizontalDivider = false,
                withPullToRefresh = false,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        state.product?.let { product ->
                            // Header section
                            item {
                                HeaderItem(
                                    product,
                                    stretchFactor,
                                    onBackClick = {
                                        scope.launch { sheetState.hide() }
                                        onAction(ProductDetailAction.OnBack)
                                    }
                                )
                            }

                            // Title section
                            item {
                                TitleItem(product = product)

                                Spacer(Modifier.height(8.dp))
                            }

                            // Options section
                            items(product.optionGroups.size) { index ->
                                val group = product.optionGroups[index]

                                OptionGroupItem(
                                    productOptionGroup = group,
                                    state = state.optionGroupStates[group.id]!!,
                                    isStoreOpen = true,
                                    onSelectionChanged = { selected ->
                                        onAction(
                                            ProductDetailAction.OnOptionGroupStateChange(
                                                selected, index
                                            )
                                        )
                                    },
                                )
                            }

                            // Quantity buttons
                            item {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                ) {
                                    TextButton(
                                        onClick = {
                                            onAction(ProductDetailAction.OnDecrement)

                                        }) {
                                        Image(
                                            painter = painterResource(R.drawable.ic_moin_product),
                                            contentDescription = null,
                                            modifier = Modifier.size(45.dp),
                                        )
                                    }

                                    Text(
                                        text = state.quantity.toString(),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        maxLines = 1,
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )

                                    TextButton(
                                        onClick = {
                                            onAction(ProductDetailAction.OnIncrement)
                                        }) {
                                        Image(
                                            painter = painterResource(R.drawable.ic_plus),
                                            contentDescription = null,
                                            modifier = Modifier.size(45.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // hidden Appbar
                    HiddenAppbar(
                        title = state.product?.name ?: "",
                        isRowVisible = isRowVisible,
                        onBackClick = {
                            scope.launch { sheetState.hide() }
                            onAction(ProductDetailAction.OnBack)
                        }
                    )

                    CartItem(
                        state = state,
                        onAction = onAction,
                        onBackClick = {
                            scope.launch { sheetState.hide() }
                            onAction(ProductDetailAction.OnBack)
                        },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderItem(
    product: Product,
    stretchFactor: Float,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .scale(stretchFactor)
    ) {

        ProductImage(product = product)

        Column(
            verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 15.dp, end = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onBackClick() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(45.dp),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(
                        color = Color.White, shape = RoundedCornerShape(
                            topStart = 20.dp, topEnd = 20.dp
                        )
                    )
            )
        }
    }
}

@Composable
fun ProductImage(
    modifier: Modifier = Modifier,
    product: Product,
) {
    val list = product.assets
    val pagerState = rememberPagerState { list.size }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (list.isEmpty()) {
            Image(
                painter = painterResource(R.drawable.product_placeholder),
                contentDescription = null,
                modifier = Modifier.then(
                    Modifier.fillMaxSize()
                ),
                contentScale = ContentScale.Crop
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = list[page].imageUrl,
                contentDescription = null,
                modifier = Modifier.then(
                    Modifier.fillMaxSize()
                ),
                placeholder = painterResource(R.drawable.product_placeholder),
                error = painterResource(R.drawable.product_placeholder),
                contentScale = ContentScale.Crop
            )
        }

        if (list.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(list.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                            .background(
                                color = if (pagerState.currentPage == index) Color.White else Color.White.copy(
                                    0.6f
                                ),
                                shape = RoundedCornerShape(50)
                            )
                    )
                    Spacer(Modifier.size(4.dp))
                }
            }
        }
    }
}

@Composable
private fun TitleItem(product: Product) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Start,
            maxLines = 2,
            lineHeight = TextUnit(32f, TextUnitType.Sp)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(start = 5.dp),
            text = product.description,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Normal
            ),
            textAlign = TextAlign.Start,
            color = Color.Black.copy(alpha = 0.8f),
        )
    }
}

@Composable
private fun HiddenAppbar(
    title: String, isRowVisible: Boolean, onBackClick: () -> Unit
) {
    AnimatedVisibility(
        visible = !isRowVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(MaterialTheme.colorScheme.background),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onBackClick() }) {
                    Icon(
                        imageVector = Icons.Sharp.Close,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(30.dp),
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp)
                )
            }
        }
    }
}


@Composable
private fun CartItem(
    modifier: Modifier = Modifier,
    state: ProductDetailState,
    onAction: (ProductDetailAction) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 30.dp)
            .padding(vertical = 10.dp)
    ) {
        DoneButton(
            onClick = {
                if (state.product != null) {
                    if (state.cartProduct != null) {
                        onAction(
                            ProductDetailAction.OnDecrementQuantity(
                                product = state.cartProduct.product,
                                productOptions = state.cartProduct.options,
                                removeAll = true,
                            )
                        )
                    }

                    onAction(
                        ProductDetailAction.OnIncrementQuantity(
                            product = state.product,
                            productOptions = state.productOptions,
                            quantity = state.quantity,
                        )
                    )

                    onBackClick()
                }
            },
            text = if (state.cartProduct != null)
                stringResource(R.string.confirm_product)
            else stringResource(R.string.add_product),
            style = MaterialTheme.typography.bodyLarge,
            enabled = state.canAddProduct,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        )
    }
}