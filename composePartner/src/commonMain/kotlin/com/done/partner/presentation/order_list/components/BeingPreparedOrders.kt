package com.done.partner.presentation.order_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.done.partner.R
import com.done.partner.domain.models.orders.Order
import com.done.core.presentation.core.design_system.DoneButton
import com.done.core.presentation.core.ui.theme.DoneTheme
import com.done.core.presentation.core.ui.theme.doneOrange
import com.done.partner.presentation.order_list.OrderListAction
import com.done.partner.presentation.order_list.OrderListState

fun LazyListScope.BeingPreparedOrders(
    orders: List<Order>,
    state: OrderListState,
    onAction: (OrderListAction) -> Unit
) {

    item {
        Text(
            text = stringResource(R.string.accepted) + if (orders.isNotEmpty()) " (" + orders.size + ")" else "",
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
            BeingPreparedOrderItem(
                modifier = Modifier.animateItem(),
                order = order,
                state = state,
                onAction = onAction,
            )
        }
    } else {
        item {
            NoOrdersCard(
                description = stringResource(R.string.you_have_no_accepted_orders),
                icon = painterResource(R.drawable.check)
            )
        }
    }
}

@Composable
fun BeingPreparedOrderItem(
    modifier: Modifier = Modifier,
    order: Order,
    state: OrderListState,
    onAction: (OrderListAction) -> Unit
) {

    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.onPrimary)
                .clickable { onAction(OrderListAction.OnOrderClick(order.id, order.status)) }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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

                Box {
                    Icon(
                        painter = painterResource(R.drawable.shopping_bag),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(34.dp)
                            .align(Alignment.Center),
                    )
                    Text(
                        text = "${order.orderItems.size}",
                        lineHeight = 1.sp,
                        color = MaterialTheme.colorScheme.background,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            DoneButton(
                modifier = Modifier.fillMaxWidth().height(65.dp),
                buttonColor = MaterialTheme.colorScheme.primary,
                isLoading = state.orderThatIsBeingMarkedAcceptedOrReadyId == order.id,
                verticalPadding = 4.dp,
                onClick = {
                    onAction(OrderListAction.OnMarkOrderAsReady(order.id))
                },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.check_circle),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.mark_as_ready),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun AcceptedOrderPreview() {
    DoneTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "#123",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Box {
                Icon(
                    painter = painterResource(R.drawable.shopping_bag),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(34.dp)
                        .align(Alignment.Center),
                )
                Text(
                    text = "12",
                    lineHeight = 1.sp,
                    color = MaterialTheme.colorScheme.background,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                )
            }
        }
    }
}