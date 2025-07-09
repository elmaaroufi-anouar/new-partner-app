package com.done.partner.presentation.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.done.partner.R

@Composable
fun Banner() {
    Box(
        modifier = Modifier
            .fillMaxHeight(0.35f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.End
        ) {

            Image(
                painter = painterResource(R.drawable.done_partner),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
        }
    }
}