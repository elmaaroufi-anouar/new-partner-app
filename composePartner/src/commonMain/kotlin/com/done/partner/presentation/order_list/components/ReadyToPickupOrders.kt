package com.done.partner.presentation.order_list.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.done.partner.domain.models.orders.Order
import com.done.partner.R
import com.done.partner.presentation.order_list.OrderListAction
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.ReadyToPickupOrders(
    orders: List<Order>,
    onAction: (OrderListAction) -> Unit
) {

    item {
        Text(
            text = stringResource(R.string.ready) + if (orders.isNotEmpty()) " (" + orders.size + ")" else "",
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
            ReadyOrderItem(
                modifier = Modifier.animateItem(),
                order = order,
                onAction = onAction,
            )
        }
    }  else {
        item {
            NoOrdersCard(
                description = stringResource(R.string.you_gave_no_ready_orders),
                icon = painterResource(R.drawable.shopping_bag)
            )
        }
    }
}