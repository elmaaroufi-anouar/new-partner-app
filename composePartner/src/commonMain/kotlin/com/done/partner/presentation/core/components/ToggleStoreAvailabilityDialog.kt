package com.done.partner.presentation.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.done.partner.R
import com.done.core.presentation.core.design_system.DoneButton
import com.done.core.presentation.core.ui.theme.DoneTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ToggleStoreAvailabilityDialog(
    modifier: Modifier = Modifier,
    isStoreOpen: Boolean,
    properties: DialogProperties = DialogProperties(),
    onToggleStoreAvailability: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    Dialog(
        properties = properties,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(100))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(60.dp),
                    painter = painterResource(R.drawable.store),
                    contentDescription = null
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = if (isStoreOpen) {
                    stringResource(R.string.close_your_store)
                } else {
                    stringResource(R.string.ready_to_open_you_store)
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (isStoreOpen) {
                    stringResource(R.string.if_you_close_your_store_you_won_t_be_receiving_any_orders)
                } else {
                    stringResource(R.string.show_customers_that_you_re_open_and_available_to_receive_orders)
                },
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )


            Spacer(Modifier.height(24.dp))

            DoneButton(
                verticalPadding = 3.dp,
                style = MaterialTheme.typography.bodyMedium,
                text = if (isStoreOpen) {
                    stringResource(R.string.close_store)
                } else {
                    stringResource(R.string.ready_to_open)
                },
                onClick = {
                    onToggleStoreAvailability()
                }
            )

            Spacer(Modifier.height(16.dp))

            Text(
                modifier = Modifier.clickable {
                    onDismiss()
                },
                text = if (isStoreOpen) {
                    stringResource(R.string.don_t_close)
                } else {
                    stringResource(R.string.not_ready_yet)
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Preview
@Composable
private fun ChangeStoreAvailabilityDialogPreview() {
    DoneTheme {
        ToggleStoreAvailabilityDialog(
            isStoreOpen = true
        )
    }
}