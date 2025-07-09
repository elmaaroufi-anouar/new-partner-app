package com.done.partner.presentation.product.products

import androidx.compose.foundation.text.input.TextFieldState
import com.done.partner.domain.models.product.Product
import com.done.core.domain.models.pagination.Pagination
import com.done.partner.domain.models.store.Store

data class ProductsState(
    val isLoadingStore: Boolean = false,
    val stores: List<Store> = emptyList(),
    val selectedStore: Store? = null,
    val selectedStoreId: String? = null,
    val isStoreOpen: Boolean = false,
    val isChangingStoreAvailability: Boolean = false,
    val isToggleStoreAvailabilityDialogShowing: Boolean = false,

    val isLoadingProducts: Boolean = true,
    val searchProductsQueryState: TextFieldState = TextFieldState(""),
    val products: List<Product> = emptyList(),
    val pagination: Pagination? = null,

    val disableProductsPeriodOptions: List<String> = listOf(
        "One Hour", "Until the end of the day", "Until tomorrow",
    ),
    val isUpdatingProductsAvailability: Boolean = false,
    val selectedProductToEnableIndex: Int? = null,
    val isDisableProductsFabShowing: Boolean = false,
    val isUndoDisableProductsSnackbarShowing: Boolean = false,

    val priceTextState: TextFieldState = TextFieldState(),
    val selectedProductToUpdatePriceIndex: Int? = null,
    val isUpdatingProductPrice: Boolean = false,
    val isUpdateProductPriceDialogShowing: Boolean = false
)