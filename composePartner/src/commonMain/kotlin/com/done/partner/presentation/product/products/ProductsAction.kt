package com.done.partner.presentation.product.products

import com.done.core.presentation.core.util.Action

sealed interface ProductsAction : Action {
    data class OnProductClick(val productId: String, val hasOptions: Boolean) : ProductsAction

    data object OnToggleStoreAvailabilityDialog : ProductsAction
    data object OnToggleStoreAvailability : ProductsAction

    data object OnPaginate : ProductsAction
    data object OnPullToRefresh : ProductsAction
    data object OnReload : ProductsAction

    data class OnSelectProductToDisable(val productIndex: Int) : ProductsAction
    data class OnUnselectProductToDisable(val productIndex: Int) : ProductsAction
    data object OnAcceptDisableSelectedProductsClick : ProductsAction
    data object OnUndoDisableSelectedProducts : ProductsAction

    data class OnEnableProduct(val productIndex: Int) : ProductsAction

    data class OnToggleUpdateProductPriceDialog(val productIndex: Int?) : ProductsAction
    data object OnUpdateProductPrice : ProductsAction
}