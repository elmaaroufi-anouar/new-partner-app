package com.done.partner.presentation.product.product_options

import com.done.core.presentation.core.util.Action

sealed interface ProductOptionsAction : Action {
    data object OnBackClick : ProductOptionsAction

    data class OnPullToRefresh(val productId: String) : ProductOptionsAction
    data class OnReload(val productId: String) : ProductOptionsAction

    data class OnSelectOptionToDisable(val optionId: String) : ProductOptionsAction
    data class OnUnselectOptionToDisable(val optionId: String) : ProductOptionsAction
    data object OnAcceptDisableSelectedOptionsClick : ProductOptionsAction
    data object OnUndoDisableSelectedOptions : ProductOptionsAction

    data class OnEnableOption(val optionId: String) : ProductOptionsAction
    data class OnEnableOptionGroup(val optionGroupId: String) : ProductOptionsAction

    data class OnToggleUpdateOptionPriceDialog(val optionId: String?) : ProductOptionsAction
    data object OnUpdateOptionPrice : ProductOptionsAction
}