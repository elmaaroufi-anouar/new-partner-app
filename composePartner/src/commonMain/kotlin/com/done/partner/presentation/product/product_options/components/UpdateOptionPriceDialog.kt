package com.done.partner.presentation.product.product_options.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.done.core.presentation.core.design_system.DoneButton
import com.done.core.presentation.core.design_system.DoneTextField
import com.done.partner.presentation.product.product_options.ProductOptionsState
import org.jetbrains.compose.resources.stringResource

@Composable
fun UpdateOptionPriceDialog(
    state: ProductOptionsState,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onPrimary)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.close),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onDismiss() }
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = stringResource(Res.string.edit_product_price),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(Modifier.height(26.dp))

            DoneTextField(
                textFieldState = state.priceTextState,
                centerText = true,
                style = MaterialTheme.typography.headlineSmall,
                keyBoardType = KeyboardType.Number
            )

            Spacer(Modifier.height(26.dp))

            DoneButton(
                text = stringResource(Res.string.save),
                onClick = { onSave() },
                isLoading = state.isUpdatingOptionPrice,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.cancel),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .alpha(if (state.isUpdatingOptionPrice) 0f else 1f)
                    .clickable {
                        if (!state.isUpdatingOptionPrice) {
                            onDismiss()
                        }
                    }
            )

            Spacer(Modifier.height(8.dp))
        }

    }

}