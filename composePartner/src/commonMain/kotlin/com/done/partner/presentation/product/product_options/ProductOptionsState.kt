package com.done.partner.presentation.product.product_options

import androidx.compose.foundation.text.input.TextFieldState
import com.done.partner.domain.models.product.Product
import com.done.partner.domain.models.product.product_option.ProductOption

data class ProductOptionsState(
    val selectedStoreId: String? = null,

    val isLoading: Boolean = true,
    val product: Product? = null,
    val options: List<ProductOption> = emptyList(),

    val isUpdatingOptionsAvailability: Boolean = false,
    val selectedOptionToEnableId: String? = null,
    val selectedOptionGroupToEnableId: String? = null,
    val isDisableOptionsFabShowing: Boolean = false,

    val priceTextState: TextFieldState = TextFieldState(),
    val selectedOptionToUpdatePriceId: String? = null,
    val isUpdatingOptionPrice: Boolean = false,
    val isUpdateOptionPriceDialogShowing: Boolean = false
)