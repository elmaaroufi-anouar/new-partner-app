package com.done.core.presentation.core.design_system

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DoneButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    verticalPadding: Dp = 6.dp,
    horizontalPadding: Dp = 6.dp,
    style: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.SemiBold
    ),
    textMaxLines: Int = 1,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    buttonColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(
                vertical = verticalPadding,
                horizontal = horizontalPadding
            ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (text != null) {
                Text(
                    text = text,
                    style = style,
                    maxLines = textMaxLines,
                    color = textColor.copy(if (isLoading) 0f else 1f)
                )
            } else {
                content()
            }
        }
    }
}


@Composable
fun DoneOutlinedButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    verticalPadding: Dp = 6.dp,
    horizontalPadding: Dp = 6.dp,
    style: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight = FontWeight.Medium,
    textMaxLines: Int = 1,
    textColor: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        border = BorderStroke(
            width = 0.5.dp,
            color = if (enabled) borderColor
            else borderColor.copy(0.3f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(
                vertical = verticalPadding, horizontal = horizontalPadding
            ),
            contentAlignment = Alignment.Center
        ) {
            if (text != null) {
                Text(
                    text = text,
                    style = style,
                    color = textColor,
                    maxLines = textMaxLines,
                    fontWeight = fontWeight,
                    modifier = Modifier.alpha(if (isLoading) 0f else 1f),
                )
            } else {
                Box(modifier = Modifier.alpha(if (isLoading) 0f else 1f)) {
                    content()
                }
            }

            CircularProgressIndicator(
                strokeWidth = 1.5.dp,
                modifier = Modifier
                    .size(15.dp)
                    .alpha(if (isLoading) 1f else 0f)
            )
        }
    }
}
