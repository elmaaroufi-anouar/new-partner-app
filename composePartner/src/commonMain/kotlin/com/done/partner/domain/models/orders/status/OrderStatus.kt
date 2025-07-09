package com.done.partner.domain.models.orders.status

object OrderStatus {
    const val PENDING = "pending"
    const val BEING_PREPARED = "being_prepared"
    const val READY_FOR_PICKUP = "ready_for_pick_up"
    const val DELIVERED = "delivered"
    const val CANCELLED = "cancelled"
}