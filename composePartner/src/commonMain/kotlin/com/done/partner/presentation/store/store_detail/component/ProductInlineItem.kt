package com.done.partner.presentation.store.store_detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.done.partner.domain.models.cart.CartProduct
import com.done.partner.domain.models.product.Product
import com.done.partner.presentation.store.component.QuantityButton
import com.done.partner.presentation.store.store_detail.StoreDetailAction
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductInlineItem(
    modifier: Modifier = Modifier,
    product: Product,
    cartProducts: List<CartProduct>?,
    onAction: (StoreDetailAction) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = if (product.assets.isNotEmpty()) product.assets.first().imageUrl else "",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(Res.drawable.product_placeholder),
                    error = painterResource(Res.drawable.product_placeholder),
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = product.description,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = product.price?.display ?: "",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                product.comparePrice?.display?.let { price ->
                    Text(
                        text = price,
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = TextDecoration.LineThrough,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                    )
                }
            }

            Box {
                if (product.optionGroups.isNotEmpty()) {
                    Image(
                        painter = painterResource(Res.drawable.ic_add_product),
                        contentDescription = null,
                        modifier = Modifier
                            .width(46.dp)
                            .height(46.dp)
                    )
                } else {
                    if (cartProducts?.isEmpty() == true || cartProducts?.get(0)?.quantity == 0) {
                        Image(
                            painter = painterResource(Res.drawable.ic_add_product),
                            contentDescription = null,
                            modifier = Modifier
                                .width(46.dp)
                                .height(46.dp)
                                .clickable {
                                    onAction(
                                        StoreDetailAction.OnIncrementQuantity(
                                            product, emptyList()
                                        )
                                    )
                                }
                        )
                    } else {
                        cartProducts?.get(0)?.let { cartProduct ->
                            QuantityButton(
                                onPlusClick = {
                                    onAction(
                                        StoreDetailAction.OnIncrementQuantity(
                                            product,
                                            emptyList()
                                        )
                                    )
                                },
                                onMinusClick = {
                                    onAction(
                                        StoreDetailAction.OnDecrementQuantity(
                                            product,
                                            emptyList()
                                        )
                                    )
                                },
                                isMinusEnabled = cartProduct.quantity > 0,
                                isPlusEnabled = true,
                                value = cartProduct.quantity.toString()
                            )
                        }
                    }
                }
            }
        }
    }

    if (product.optionGroups.isNotEmpty()) {
        cartProducts?.let {
            Column {
                cartProducts.forEach { cartProduct ->
                    ProductVariant(
                        cartProduct,
                        onAction,
                    )
                }
            }
        }
    }
}