package com.done.core.presentation.core.design_system

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DoneAlertDialog(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    primaryButtonText: String,
    secondaryButtonText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    DoneDialog(
        modifier = modifier,
        title = title,
        description = description,
        onDismiss = { onDismiss() },
        betweenButtonsPadding = 16.dp,
        primaryButton = {
            DoneButton(
                modifier = Modifier.weight(1f),
                onClick = { onDismiss() },
                text = primaryButtonText
            )
        },
        secondaryButton = {
            DoneOutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    onConfirm()
                },
                text = secondaryButtonText
            )
        }
    )
}