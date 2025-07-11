package com.done.partner.presentation.product.product_options

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.done.partner.domain.models.price.Price
import com.done.partner.domain.models.product.product_option.ProductOption
import com.done.core.presentation.core.design_system.DoneOutlinedButton
import com.done.core.presentation.core.design_system.DoneScaffold
import com.done.core.presentation.core.design_system.DoneTopBar
import com.done.core.presentation.core.ui.components.ObserveAsEvent
import com.done.core.presentation.core.ui.components.networkErrorToast
import com.done.core.presentation.core.ui.theme.DoneTheme
import com.done.core.presentation.core.ui.theme.doneBackgroundOrange
import com.done.core.presentation.core.ui.theme.doneGreen
import com.done.partner.presentation.product.product_options.components.DisableOptionsFab
import com.done.partner.presentation.product.product_options.components.UpdateOptionPriceDialog
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductOptionsScreenCore(
    viewModel: ProductOptionsViewModel = koinViewModel(),
    productId: String,
    onBackClick: () -> Unit
) {

    LaunchedEffect(true) {
        viewModel.onAction(ProductOptionsAction.OnReload(productId))
    }

    val context = LocalContext.current

    ObserveAsEvent(viewModel.event) { event ->
        when (event) {
            is OptionsEvent.Error -> {
                networkErrorToast(
                    networkError = event.networkError,
                    context = context,
                )
            }
        }
    }

    ProductOptionsScreen(
        state = viewModel.state,
        productId = productId,
        onAction = { action ->
            when (action) {
                ProductOptionsAction.OnBackClick -> onBackClick()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductOptionsScreen(
    state: ProductOptionsState,
    productId: String,
    onAction: (ProductOptionsAction) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    DoneScaffold(
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
            state = rememberTopAppBarState()
        ),
        topBar = { topAppBarScrollBehavior ->
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary)
            ) {
                DoneTopBar(
                    scrollBehavior = topAppBarScrollBehavior,
                    titleText = stringResource(Res.string.options),
                    navigationIcon = Icons.AutoMirrored.Default.ArrowBackIos,
                    onNavigationClick = {
                        onAction(ProductOptionsAction.OnBackClick)
                    }
                )

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = state.product?.assets?.firstOrNull()?.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.onBackground.copy(0.1f))
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = state.product?.name ?: "",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        onPullToRefresh = {
            onAction(ProductOptionsAction.OnPullToRefresh(productId))
        }
    ) { padding ->

        Box(
            modifier = Modifier.padding(padding)
        ) {
            Column {
                Box {
                    if (state.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (state.product == null && state.options.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(Res.string.couldn_t_load_product))
                        }
                    } else {
                        ProductOptions(
                            state = state,
                            onAction = onAction,
                        )
                    }
                }
            }

            if (state.isDisableOptionsFabShowing) {
                DisableOptionsFab(
                    onClick = {
                        onAction(ProductOptionsAction.OnAcceptDisableSelectedOptionsClick)
                    }
                )
            }

            if (state.isUpdateOptionPriceDialogShowing) {
                UpdateOptionPriceDialog(
                    state = state,
                    onDismiss = {
                        onAction(ProductOptionsAction.OnToggleUpdateOptionPriceDialog(null))
                    },
                    onSave = {
                        onAction(ProductOptionsAction.OnUpdateOptionPrice)
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductOptions(
    modifier: Modifier = Modifier,
    state: ProductOptionsState,
    onAction: (ProductOptionsAction) -> Unit
) {

    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 22.dp, bottom = 150.dp)
    ) {
        val groupedOptions = state.options.groupBy { Pair(it.optionGroupId, it.optionGroupName) }
        itemsIndexed(groupedOptions.keys.toList()) { index, optionGroup ->
            val options = groupedOptions[optionGroup]
            if (options?.isNotEmpty() == true) {
                OptionGroupItem(
                    optionGroup = optionGroup,
                    options = options,
                    state = state,
                    onAction = onAction
                )
                Spacer(Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun OptionGroupItem(
    modifier: Modifier = Modifier,
    optionGroup: Pair<String, String>,
    options: List<ProductOption>,
    state: ProductOptionsState,
    onAction: (ProductOptionsAction) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (options.none { it.isEnabled })
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .animateContentSize()
            .clickable {
                isExpanded = !isExpanded
            }
    ) {
        Spacer(Modifier.size(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = optionGroup.second,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )


            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = {
                    isExpanded = !isExpanded
                }
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp
                    else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            Spacer(Modifier.width(8.dp))
        }

        if (options.none { it.isEnabled }) {
            Spacer(Modifier.height(6.dp))
            DoneOutlinedButton(
                text = stringResource(Res.string.enable),
                textColor = doneGreen,
                style = MaterialTheme.typography.bodyLarge,
                isLoading = state.selectedOptionGroupToEnableId == optionGroup.first,
                borderColor = doneGreen,
                onClick = {
                    onAction(ProductOptionsAction.OnEnableOptionGroup(optionGroup.first))
                },
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(Modifier.height(6.dp))
        }

        if (isExpanded) {
            Spacer(Modifier.size(12.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground.copy(0.1f)
            )
            Column(
                modifier = Modifier
                    .clickable(enabled = false) {}
            ) {
                options.forEachIndexed { index, option ->
                    OptionItem(
                        option = option,
                        state = state,
                        onAction = onAction
                    )
                    if (index != options.lastIndex) {
                        HorizontalDivider(Modifier.alpha(0.3f))
                    }
                }
            }
        } else {
            Spacer(Modifier.size(12.dp))
        }
    }
}

@Composable
private fun OptionItem(
    modifier: Modifier = Modifier,
    option: ProductOption,
    state: ProductOptionsState,
    onAction: (ProductOptionsAction) -> Unit
) {
    Row(
        modifier = modifier
            .background(
                if (option.isEnabled) {
                    MaterialTheme.colorScheme.onBackground.copy(0.02f)
                } else {
                    doneBackgroundOrange
                }
            )
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier.size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isUpdatingOptionsAvailability && option.isSelectedToDisable) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Checkbox(
                    checked = option.isSelectedToDisable,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            onAction(ProductOptionsAction.OnSelectOptionToDisable(option.id))
                        } else {
                            onAction(ProductOptionsAction.OnUnselectOptionToDisable(option.id))
                        }
                    },
                    enabled = option.isEnabled,
                    modifier = Modifier.alpha(if (option.isEnabled) 1.0f else 0.1f)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .weight(1f)
        ) {
            Column {
                Text(
                    text = option.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(Modifier.height(8.dp))

                TextButton(
                    modifier = Modifier,
                    onClick = {
                        onAction(ProductOptionsAction.OnToggleUpdateOptionPriceDialog(option.id))
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(0.9f)
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = option.additionalPrice?.display ?: "",
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

                Spacer(Modifier.height(16.dp))
            }

            if (!option.isEnabled) {
                DoneOutlinedButton(
                    text = stringResource(Res.string.enable),
                    textColor = doneGreen,
                    style = MaterialTheme.typography.bodyLarge,
                    isLoading = state.selectedOptionToEnableId == option.id || state.isUpdatingOptionsAvailability && option.isSelectedToDisable,
                    borderColor = doneGreen,
                    onClick = {
                        onAction(ProductOptionsAction.OnEnableOption(option.id))
                    }
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
private fun ProductOptionsScreenPreview() {
    DoneTheme {
        ProductOptionsScreen(
            state = ProductOptionsState(
                isLoading = false,
                options = listOf(
                    ProductOption(
                        id = "11",
                        name = "option 11",
                        isEnabled = false,
                        isSelectedToDisable = false,
                        additionalPrice = Price(
                            amount = 100.0,
                            currency = "EGP",
                            display = "100 EGP"
                        ),
                        chooseMoreThanOnce = true,
                        optionGroupId = "1",
                        optionGroupName = "group 1",
                        createdAt = "",
                        updatedAt = ""
                    ),
                    ProductOption(
                        id = "12",
                        name = "option 12",
                        isEnabled = false,
                        isSelectedToDisable = false,
                        additionalPrice = Price(
                            amount = 100.0,
                            currency = "EGP",
                            display = "100 EGP"
                        ),
                        chooseMoreThanOnce = true,
                        optionGroupId = "1",
                        optionGroupName = "group 1",
                        createdAt = "",
                        updatedAt = ""
                    ),
                    ProductOption(
                        id = "31",
                        name = "option 31",
                        isEnabled = true,
                        isSelectedToDisable = false,
                        additionalPrice = Price(
                            amount = 100.0,
                            currency = "EGP",
                            display = "100 EGP"
                        ),
                        chooseMoreThanOnce = true,
                        optionGroupId = "3",
                        optionGroupName = "group 3",
                        createdAt = "",
                        updatedAt = ""
                    ),
                    ProductOption(
                        id = "21",
                        name = "option 21",
                        isEnabled = true,
                        isSelectedToDisable = false,
                        additionalPrice = Price(
                            amount = 100.0,
                            currency = "EGP",
                            display = "100 EGP"
                        ),
                        chooseMoreThanOnce = true,
                        optionGroupId = "2",
                        optionGroupName = "group 2",
                        createdAt = "",
                        updatedAt = ""
                    ),
                    ProductOption(
                        id = "22",
                        name = "option 22",
                        isEnabled = true,
                        isSelectedToDisable = false,
                        additionalPrice = Price(
                            amount = 100.0,
                            currency = "EGP",
                            display = "100 EGP"
                        ),
                        chooseMoreThanOnce = true,
                        optionGroupId = "2",
                        optionGroupName = "group 2",
                        createdAt = "",
                        updatedAt = ""
                    )
                )
            ),
            productId = "",
            onAction = {}
        )
    }
}