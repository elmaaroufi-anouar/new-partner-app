package com.done.partner.presentation.order_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.done.partner.domain.models.orders.Order
import com.done.partner.domain.models.orders.previewOrders
import com.done.core.presentation.core.design_system.DoneButton
import com.done.core.presentation.core.ui.theme.DoneTheme
import com.done.core.presentation.core.ui.theme.doneGreen
import com.done.core.presentation.core.ui.theme.doneLighterOrange
import com.done.core.presentation.core.ui.theme.doneOrange
import com.done.partner.presentation.order_list.OrderListAction
import com.done.partner.presentation.order_list.OrderListState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

fun LazyListScope.PendingOrders(
    orders: List<Order>,
    state: OrderListState,
    onAction: (OrderListAction) -> Unit
) {

    item {
        Text(
            text = stringResource(Res.string.pending) + if (orders.isNotEmpty()) " (" + orders.size + ")" else "",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .padding(top = 20.dp)
        )
    }

    if (orders.isNotEmpty()) {
        itemsIndexed(orders) { index, order ->
            PendingOrderItem(
                modifier = Modifier.animateItem(),
                order = order,
                state = state,
                onAction = onAction
            )
        }

    } else {
        item {
            NoOrdersCard(
                description = stringResource(Res.string.you_have_no_pending_orders),
                icon = painterResource(Res.drawable.pending)
            )
        }
    }
}

@Composable
fun PendingOrderItem(
    modifier: Modifier = Modifier,
    order: Order,
    state: OrderListState,
    onAction: (OrderListAction) -> Unit
) {

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = doneOrange,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(doneLighterOrange)
            .clickable { onAction(OrderListAction.OnOrderClick(order.id, order.status)) }
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${order.friendlyNumber}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(Modifier.width(10.dp))

                if (order.inStorePickup) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.DirectionsWalk,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                doneOrange.copy(0.2f),
                                RoundedCornerShape(50)
                            )
                            .padding(4.dp),
                    )
                }
            }

            val date = if (order.createdAt.length > 4) {
                order.createdAt.take(10)
            } else if (order.createdAt.isNotEmpty()) {
                stringResource(Res.string.min_ago, order.createdAt)
            } else {
                ""
            }
            Text(
                text = date,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        if (order.orderItems.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            order.orderItems.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${item.quantity}x  ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Text(
                        text = item.price?.display ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))
            }
        }

        if (order.productAmount != null) {

            Spacer(Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.total_price),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
                Text(
                    text = order.productAmount?.display ?: "",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        DoneButton(
            modifier = Modifier.fillMaxWidth(),
            buttonColor = doneGreen,
            isLoading = state.orderThatIsBeingMarkedAcceptedOrReadyId == order.id,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            text = stringResource(Res.string.accept),
            onClick = {
                onAction(
                    OrderListAction.OnAcceptOrder(order = order)
                )
            }
        )
    }
}

@Preview
@Composable
private fun PendingOrderItemPreview() {
    DoneTheme {
        PendingOrderItem(
            order = previewOrders[0],
            state = OrderListState(),
            onAction = {}
        )
    }
}