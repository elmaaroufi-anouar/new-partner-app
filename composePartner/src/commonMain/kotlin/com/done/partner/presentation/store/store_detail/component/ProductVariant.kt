package com.done.partner.presentation.store.store_detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.done.partner.domain.models.cart.CartProduct
import com.done.partner.presentation.store.store_detail.StoreDetailAction
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductVariant(
    cartProduct: CartProduct,
    onAction: (StoreDetailAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.onBackground.copy(0.07f))
            .clickable {
                onAction(
                    StoreDetailAction.OnOpenProductSheet(
                        product = cartProduct.product, cartProduct = cartProduct
                    )
                )
            },
    ) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = Color.LightGray.copy(alpha = 0.4f)
        )

        Column(modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${cartProduct.quantity}x",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(0.1f)
                )

                Column(modifier = Modifier.weight(0.6f)) {
                    Text(
                        text = cartProduct.product.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal
                        ),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = cartProduct.options
                            .mapNotNull { option ->
                                cartProduct.product.optionGroups.find { it.id == option.groupId }
                                    ?.options
                                    ?.find { it.id == option.id }
                                    ?.name
                            }
                            .joinToString(", "),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Light
                        ),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Gray,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                    .height(34.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = {
                        onAction(
                            StoreDetailAction.OnDecrementQuantity(
                                cartProduct.product, cartProduct.options
                            )
                        )
                }, contentPadding = PaddingValues()) {
                    Image(
                        painter = painterResource(Res.drawable.ic_moin_primary),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                }

                TextButton(
                    onClick = {
                        onAction(
                            StoreDetailAction.OnIncrementQuantity(
                                cartProduct.product, cartProduct.options
                            )
                        )
                }, contentPadding = PaddingValues()) {
                    Image(
                        painter = painterResource(Res.drawable.ic_plus_primary),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        }
    }
}