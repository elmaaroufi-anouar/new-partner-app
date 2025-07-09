package com.done.partner.presentation.store.product_detail.component

import com.done.partner.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.done.partner.domain.models.product.product_option.ProductOptionGroup
import com.done.partner.presentation.store.product_detail.OptionGroupState
import com.done.partner.presentation.store.product_detail.SelectionType
import com.done.partner.presentation.store.component.QuantityButton
import org.jetbrains.compose.resources.stringResource


@Composable
fun OptionGroupItem(
    productOptionGroup: ProductOptionGroup,
    state: OptionGroupState,
    isStoreOpen: Boolean,
    onSelectionChanged: (OptionGroupState) -> Unit,
) {
    val selectionType = if (productOptionGroup.maxOptions == 1) SelectionType.Radio
    else if (productOptionGroup.options.isNotEmpty() && productOptionGroup.options[0].chooseMoreThanOnce) SelectionType.Button
    else SelectionType.Checkbox

    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Text(
            text = productOptionGroup.name,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(bottom = 5.dp)
                .padding(horizontal = 20.dp),
        )

        Row(
            modifier = Modifier
                .padding(bottom = 5.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (productOptionGroup.maxOptions > 1) {
                    stringResource(
                        R.string.choose_a_maximum_of_products, productOptionGroup.maxOptions
                    )
                } else {
                    stringResource(R.string.choose_one)
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Start,
                color = Color.Gray,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 300.dp)
            )

            Spacer(Modifier.width(6.dp))

            if (productOptionGroup.isRequired) {
                Text(
                    text = stringResource(R.string.required),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(corner = CornerSize(6.dp))
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        when (selectionType) {
            SelectionType.Radio -> {
                if (state is OptionGroupState.Radio) {
                    Column {
                        productOptionGroup.options.forEach { option ->
                            val isOptionSelected = option.id == state.selectedOption
                            val isOneOptionSelected = state.selectedOption != null

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = isStoreOpen) {
                                        if (isOptionSelected) {
                                            onSelectionChanged(state.copyAsRadio(null))
                                        } else {
                                            onSelectionChanged(state.copyAsRadio(option.id))
                                        }
                                    }
                                    .padding(vertical = 15.dp, horizontal = 20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = option.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isOptionSelected) FontWeight.SemiBold else FontWeight.Normal
                                        ),
                                        textAlign = TextAlign.Start,
                                        maxLines = 2,
                                        color = if (isOptionSelected) MaterialTheme.colorScheme.onBackground
                                        else if (isOneOptionSelected) MaterialTheme.colorScheme.onBackground.copy(0.2f)
                                        else MaterialTheme.colorScheme.onBackground.copy(0.5f),
                                        modifier = Modifier.weight(0.7f)
                                    )

                                    Text(
                                        text = option.additionalPrice?.display ?: "",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        textAlign = TextAlign.End,
                                        maxLines = 1,
                                        color = if (isOptionSelected) MaterialTheme.colorScheme.onBackground
                                        else if (isOneOptionSelected) MaterialTheme.colorScheme.onBackground.copy(0.2f)
                                        else MaterialTheme.colorScheme.onBackground.copy(0.5f),
                                        modifier = Modifier
                                            .padding(horizontal = 15.dp)
                                            .weight(0.7f)
                                    )
                                }

                                Image(
                                    painter = if (isOptionSelected) painterResource(
                                        R.drawable.radio_button_checked
                                    )
                                    else painterResource(R.drawable.ic_radio_button),
                                    contentDescription = null,
                                    modifier = Modifier.size(34.dp),
                                    colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply {
                                        setToSaturation(if (isStoreOpen) 1f else 0f)
                                    }),
                                )
                            }
                            HorizontalDivider(
                                color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp
                            )
                        }
                    }
                }
            }

            SelectionType.Checkbox -> {
                if (state is OptionGroupState.Checkbox) {
                    Column {
                        productOptionGroup.options.forEach { option ->
                            val isOptionEnabled =
                                !(state.checkedItems[option.id] == false && state.checkedItems.entries.count { it.value } > productOptionGroup.maxOptions - 1)
                            val isItemSelected = state.checkedItems[option.id] == true

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable(enabled = isOptionEnabled && isStoreOpen) {
                                        val updatedCheckedItems = state.checkedItems.toMutableMap()
                                        updatedCheckedItems[option.id] =
                                            !updatedCheckedItems[option.id]!!
                                        onSelectionChanged(state.copyAsCheckbox(updatedCheckedItems))
                                    }
                                    .padding(vertical = 15.dp, horizontal = 20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = option.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isItemSelected) FontWeight.SemiBold
                                            else FontWeight.Normal
                                        ),
                                        maxLines = 2,
                                        textAlign = TextAlign.Start,
                                        color = if (isItemSelected) MaterialTheme.colorScheme.onBackground
                                        else if (isOptionEnabled) MaterialTheme.colorScheme.onBackground.copy(0.5f)
                                        else MaterialTheme.colorScheme.onBackground.copy(0.2f),
                                    )

                                    Text(
                                        text = option.additionalPrice?.display ?: "",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        textAlign = TextAlign.Start,
                                        maxLines = 1,
                                        color = if (isItemSelected) MaterialTheme.colorScheme.onBackground
                                        else if (isOptionEnabled) MaterialTheme.colorScheme.onBackground.copy(0.5f)
                                        else MaterialTheme.colorScheme.onBackground.copy(0.2f),
                                        modifier = Modifier.padding(horizontal = 15.dp)
                                    )
                                }

                                Image(
                                    painter = if (state.checkedItems[option.id] == true) painterResource(
                                        R.drawable.ic_checkbox_checked
                                    )
                                    else painterResource(R.drawable.ic_checkbox),
                                    contentDescription = null,
                                    modifier = Modifier.size(34.dp),
                                    colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply {
                                        setToSaturation(if (isStoreOpen) 1f else 0f)
                                    }),
                                )
                            }

                            HorizontalDivider(
                                color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp
                            )
                        }
                    }
                }
            }

            SelectionType.Button -> {
                if (state is OptionGroupState.Button) {
                    Column {
                        productOptionGroup.options.forEach { option ->
                            val isOptionEnabled =
                                state.quantities.entries.sumOf { it.value } < productOptionGroup.maxOptions
                            val isOptionHasQuantity = (state.quantities[option.id] ?: 0) > 0

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 15.dp, horizontal = 20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = option.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isOptionHasQuantity) FontWeight.SemiBold else FontWeight.Normal
                                        ),
                                        maxLines = 2,
                                        textAlign = TextAlign.Start,
                                        color = if (isOptionHasQuantity) MaterialTheme.colorScheme.onBackground
                                        else if (isOptionEnabled) MaterialTheme.colorScheme.onBackground.copy(0.5f)
                                        else MaterialTheme.colorScheme.onBackground.copy(0.2f),
                                    )

                                    Text(
                                        text = option.additionalPrice?.display ?: "",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        textAlign = TextAlign.Start,
                                        maxLines = 1,
                                        color = if (isOptionHasQuantity) MaterialTheme.colorScheme.primary
                                        else if (isOptionEnabled) MaterialTheme.colorScheme.onBackground.copy(0.5f)
                                        else MaterialTheme.colorScheme.onBackground.copy(0.2f),
                                        modifier = Modifier.padding(horizontal = 15.dp)
                                    )
                                }

                                QuantityButton(
                                    onPlusClick = {
                                        val updatedQuantities = state.quantities.toMutableMap()
                                        updatedQuantities[option.id] =
                                            (updatedQuantities[option.id] ?: 0) + 1
                                        onSelectionChanged(state.copyAsButton(updatedQuantities))
                                    },
                                    onMinusClick = {
                                        val updatedQuantities = state.quantities.toMutableMap()
                                        val currentQuantity = updatedQuantities[option.id] ?: 0
                                        if (currentQuantity > 0) {
                                            updatedQuantities[option.id] = currentQuantity - 1
                                            onSelectionChanged(state.copyAsButton(updatedQuantities))
                                        }
                                    },
                                    isMinusEnabled = isOptionHasQuantity && isStoreOpen,
                                    isPlusEnabled = isOptionEnabled && isStoreOpen,
                                    value = (state.quantities[option.id] ?: 0).toString()
                                )
                            }

                            HorizontalDivider(
                                color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
}
