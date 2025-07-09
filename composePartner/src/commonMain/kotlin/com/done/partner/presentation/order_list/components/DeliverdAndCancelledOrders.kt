package com.done.partner.presentation.order_list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.done.partner.domain.models.orders.Order
import com.done.partner.R
import com.done.partner.presentation.order_list.OrderListAction
import com.done.partner.presentation.order_list.OrderListState
import com.done.partner.domain.models.orders.status.OrderStatus
import com.done.core.presentation.core.ui.theme.doneBackgroundOrange
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.DeliverdAndCancelledOrders(
    orders: List<Order>,
    state: OrderListState,
    onAction: (OrderListAction) -> Unit
) {

    stickyHeader {
        Text(
            text = stringResource(R.string.delivered_canceled) + if (orders.isNotEmpty()) " (" + orders.size + ")" else "",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(doneBackgroundOrange)
                .padding(horizontal = 18.dp)
                .padding(top = 20.dp, bottom = 8.dp)
        )
    }

    if (orders.isNotEmpty()) {
        itemsIndexed(orders) { index, order ->
            if (order.status == OrderStatus.DELIVERED) {
                DeliveredOrderItem(
                    modifier = Modifier.animateItem(),
                    order = order,
                    state = state,
                    showDriver = false,
                    onAction = onAction
                )
            }
            if (order.status == OrderStatus.CANCELLED) {
                CancelledOrderItem(
                    modifier = Modifier.animateItem(),
                    order = order,
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}