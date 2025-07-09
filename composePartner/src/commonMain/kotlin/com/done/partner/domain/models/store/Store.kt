package com.done.partner.domain.models.store

import com.done.partner.domain.models.orders.StoreSection

data class Store(
    val id: String,
    val type: String,
    val createdAt: String,
    val updatedAt: String,
    val businessEmail: String,
    val invoiceEmail: String,
    val disabledAt: String?,
    val storeSections: List<StoreSection>,
    val iceFilePath: String,
    val ribFilePath: String,
    val longitude: String,
    val latitude: String,
    val storeBrand: StoreBrand?
)