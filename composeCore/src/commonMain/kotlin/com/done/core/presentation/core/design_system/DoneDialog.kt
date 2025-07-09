package com.done.core.presentation.core.design_system

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DoneDialog(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    image: Painter? = null,
    descriptionTextAlign: TextAlign = TextAlign.Center,
    onDismiss: () -> Unit = {},
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    imageWidth: Dp = 150.dp,
    betweenButtonsPadding: Dp = 0.dp,
    primaryButton: @Composable RowScope.() -> Unit = {},
    secondaryButton: @Composable RowScope.() -> Unit = {},
) {
    Dialog(
        properties = properties,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (image != null) {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier.width(imageWidth)
                )
            }
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = description,
                textAlign = descriptionTextAlign,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                secondaryButton()
                if (betweenButtonsPadding != 0.dp) {
                    Spacer(Modifier.width(betweenButtonsPadding))
                }
                primaryButton()
            }
        }
    }
}