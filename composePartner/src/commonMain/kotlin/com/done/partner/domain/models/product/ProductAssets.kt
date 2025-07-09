package com.done.partner.domain.models.product

data class ProductAsset(
    val createdAt: String,
    val imageUrl: String,
    val imageType: String,
    val id: String,
    val productId: String,
    val resourceType: String,
    val updatedAt: String
)