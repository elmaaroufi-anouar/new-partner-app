package com.done.partner.domain.models.product.product_option

import com.done.partner.domain.models.price.Price

data class ProductOption(
    val id: String,
    val name: String,
    val isEnabled: Boolean,
    val isSelectedToDisable: Boolean,
    val additionalPrice: Price?,
    val chooseMoreThanOnce: Boolean,
    val optionGroupId: String,
    val optionGroupName: String,
    val createdAt: String,
    val updatedAt: String,
    val indexForUpdate: Int = 0
)