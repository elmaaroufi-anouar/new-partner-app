package com.done.partner.domain.models.orders.status

object MarkOrderAsStatus {
    const val BEING_PREPARED = "mark-as-being-prepared"
    const val READY_FOR_PICKUP = "mark-as-ready-for-pickup"
    const val ARRIVED = "mark-as-arrived"
    const val ON_THE_WAY = "mark-as-on-the-way"
    const val DELIVERED = "mark-as-delivered"
    const val CANCELLED = "mark-as-cancelled"
}