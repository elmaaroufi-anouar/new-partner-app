package com.done.partner.presentation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.done.core.presentation.core.design_system.DoneButton
import com.done.core.presentation.core.design_system.DoneTextField
import com.done.partner.R

@Composable
fun DeliveryCodeDialog(
    textFieldState: TextFieldState,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
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
        ) {

            IconButton(
                onClick =  onDismiss
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }

            Spacer(Modifier.width(6.dp))

            Text(
                text = stringResource(R.string.enter_delivery_confirmation_code),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(26.dp))

            DoneTextField(
                textFieldState = textFieldState,
                centerText = true,
                style = MaterialTheme.typography.headlineSmall,
                focus = true,
                keyBoardType = KeyboardType.Companion.Number,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(26.dp))

            DoneButton(
                text = stringResource(R.string.confirm),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                isLoading = isLoading,
                enabled = textFieldState.text.length == 3,
                onClick = {
                    onConfirm()
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(70.dp)
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}