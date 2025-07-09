package com.done.partner.presentation.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.done.partner.R
import com.done.core.presentation.core.ui.theme.DoneTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ToggleStoreAvailabilityButton(
    modifier: Modifier = Modifier,
    storeExists: Boolean,
    isStoreOpen: Boolean,
    isLoading: Boolean,
    onToggleStoreAvailabilityDialog: () -> Unit,
) {
    Row(
        modifier = modifier
            .defaultMinSize(minWidth = 120.dp)
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                if (!isLoading && storeExists) {
                    onToggleStoreAvailabilityDialog()
                }
            }
            .padding(8.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp)
            )
        } else {

            Image(
                modifier = Modifier
                    .size(22.dp)
                    .alpha(if (storeExists) 1f else 0.2f),
                painter = if (isStoreOpen) {
                    painterResource(R.drawable.store_open)
                } else {
                    painterResource(R.drawable.store_closed)
                },
                contentDescription = if (isStoreOpen) {
                    stringResource(R.string.store_is_open)
                } else {
                    stringResource(R.string.store_is_closed)
                }
            )

            Spacer(Modifier.width(8.dp))

            Text(
                modifier = Modifier
                    .alpha(if (storeExists) 1f else 0.2f),
                text = if (!storeExists) {
                    stringResource(R.string.unavailable)
                } else if (isStoreOpen) {
                    stringResource(R.string.opened)
                } else {
                    stringResource(R.string.closed)
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Preview
@Composable
private fun ToggleStoreAvailabilityButtonPreview() {
    DoneTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ToggleStoreAvailabilityButton(
                storeExists = true,
                isStoreOpen = true,
                isLoading = false,
                onToggleStoreAvailabilityDialog = {}
            )
        }
    }
}