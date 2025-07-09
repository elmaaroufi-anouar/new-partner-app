package com.done.partner.presentation.store.component

import com.done.partner.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource

@Composable
fun QuantityButton(
    modifier: Modifier = Modifier,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    isMinusEnabled: Boolean,
    isPlusEnabled: Boolean,
    value: String
) {
    if (isMinusEnabled) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background)
                .height(40.dp)
                .widthIn(max = 130.dp),
        ) {

            IconButton(
                onClick = {
                    onMinusClick()
                },
                enabled = isMinusEnabled,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.3f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_moins),
                    contentDescription = null,
                    tint = if (isMinusEnabled){
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(0.3f)
                    },
                    modifier = Modifier.size(15.dp)
                )
            }

            Text(
                text = value,
                modifier = Modifier.width(24.dp),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center,
            )

            IconButton(
                onClick = {
                    onPlusClick()
                },
                enabled = isPlusEnabled,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.3f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus_no_border),
                    contentDescription = null,
                    tint = if (isPlusEnabled) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(0.3f)
                    },
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    } else {
        TextButton(
            onClick = {
                onPlusClick()
            },
            contentPadding = PaddingValues(),
            enabled = isPlusEnabled,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .size(34.dp)
        ) {
            Image(
                painter = if (isPlusEnabled) painterResource(R.drawable.bg_add_product) else
                    painterResource(R.drawable.bg_add_product_gray),
                contentDescription = null,
            )
        }
    }
}