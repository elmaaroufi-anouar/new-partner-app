package com.done.partner.domain.models.product

import com.done.partner.domain.models.price.Price
import com.done.partner.domain.models.product.product_option.ProductOptionGroup

data class Product(
    val averagePreparationTime: Int,
    val createdAt: String,
    val description: String,
    val isEnabled: Boolean,
    val isSelectedToDisable: Boolean,
    val id: String,
    val name: String,
    val numberOfCalories: Int,
    val price: Price?,
    val comparePrice: Price?,
    val resourceType: String,
    val storeId: String,
    val updatedAt: String,
    val assets: List<ProductAsset>,
    val optionGroups: List<ProductOptionGroup>,
)