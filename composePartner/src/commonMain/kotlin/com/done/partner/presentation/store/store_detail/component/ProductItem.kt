package com.done.partner.presentation.store.store_detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.done.partner.domain.models.cart.CartProduct
import com.done.partner.domain.models.product.Product
import com.done.partner.presentation.store.component.QuantityButton
import com.done.partner.presentation.store.store_detail.StoreDetailAction
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductItem(
    modifier: Modifier = Modifier,
    product: Product,
    cartProducts: List<CartProduct>?,
    imageHeight: Dp = 130.dp,
    onAction: (StoreDetailAction) -> Unit,
    isAddButtonShown: Boolean = true
) {
    Card(
        modifier = modifier.padding(0.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors().copy(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.height(imageHeight)) {
                AsyncImage(
                    model = if (product.assets.isNotEmpty()) product.assets.first().imageUrl else "",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.onBackground.copy(0.3f)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.product_placeholder),
                    error = painterResource(R.drawable.product_placeholder),
                )

                if (isAddButtonShown) {
                    if (product.optionGroups.isNotEmpty()) {
                        Image(
                            painter = painterResource(R.drawable.ic_add_product),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(5.dp)
                                .width(40.dp)
                                .height(40.dp)
                                .align(Alignment.BottomEnd)
                        )
                    } else {
                        if (cartProducts?.isEmpty() == true || cartProducts?.get(0)?.quantity == 0) {
                            Image(
                                painter = painterResource(R.drawable.ic_add_product),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(5.dp)
                                    .width(40.dp)
                                    .height(40.dp)
                                    .align(Alignment.BottomEnd)
                                    .clickable {
                                        onAction(
                                            StoreDetailAction.OnIncrementQuantity(
                                                product, emptyList()
                                            )
                                        )
                                    },
                            )
                        } else {
                            cartProducts?.get(0)?.let { cartProduct ->
                                QuantityButton(
                                    onPlusClick = {
                                        onAction(
                                            StoreDetailAction.OnIncrementQuantity(
                                                product, emptyList()
                                            )
                                        )
                                    },
                                    onMinusClick = {
                                        onAction(
                                            StoreDetailAction.OnDecrementQuantity(
                                                product, emptyList()
                                            )
                                        )
                                    },
                                    isMinusEnabled = cartProduct.quantity > 0,
                                    isPlusEnabled = true,
                                    value = cartProduct.quantity.toString(),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )

            Text(
                text = product.price?.display ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            product.comparePrice?.display?.let { price ->
                Text(
                    text = price,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = TextDecoration.LineThrough,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                )
            }
        }
    }
}