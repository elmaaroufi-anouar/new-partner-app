package com.done.partner.presentation.store.store_detail.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShadowDivider(
    modifier: Modifier = Modifier,
    elevation: Dp = 1.dp,
    color: Color,
    shape: Shape = RectangleShape
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .shadow(elevation = elevation, shape = shape, clip = false, spotColor = color)
    )
}