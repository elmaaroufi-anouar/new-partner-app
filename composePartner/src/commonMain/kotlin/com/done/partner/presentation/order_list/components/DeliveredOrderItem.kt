package com.done.partner.presentation.order_list.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.done.partner.R
import com.done.partner.domain.models.orders.Order
import com.done.partner.presentation.order_list.OrderListAction
import com.done.partner.presentation.order_list.OrderListState
import org.jetbrains.compose.resources.painterResource

@Composable
fun DeliveredOrderItem(
    modifier: Modifier = Modifier,
    order: Order,
    state: OrderListState,
    showDriver: Boolean = true,
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
                .padding(16.dp)
                .then(
                    if (!showDriver) Modifier.padding(vertical = 12.dp)
                    else Modifier
                ),
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "#${order.friendlyNumber}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Icon(
                    painter = painterResource(Res.drawable.check_circle),
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(27.dp)
                )
            }

            if (showDriver) {
                Spacer(Modifier.height(12.dp))
                if (order.driver != null) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.driver),
                            contentDescription = null,
                            modifier = Modifier.size(27.dp)
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = "${order.driver?.firstName} ${order.driver?.lastName?.take(1)}"
                                    + if (order.driver?.lastName?.isNotEmpty() == true) "." else "",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                } else {
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}