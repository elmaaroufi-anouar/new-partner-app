package com.done.partner.data.dto.product

import com.done.partner.data.dto.price.toPrice
import com.done.partner.domain.models.product.Product
import com.done.partner.domain.models.product.ProductAsset
import com.done.partner.domain.models.product.product_option.ProductOption
import com.done.partner.domain.models.product.product_option.ProductOptionGroup

fun ProductDataDto.toProduct(): Product {
    return Product(
        averagePreparationTime = averagePreparationTime ?: 0,
        createdAt = createdAt ?: "",
        description = description ?: "",
        isEnabled = disabledAt == null,
        isSelectedToDisable = false,
        id = id ?: "",
        name = name ?: "",
        numberOfCalories = numberOfCalories ?: 0,
        price = price?.toPrice(),
        resourceType = resourceType ?: "",
        storeId = storeId ?: "",
        comparePrice = comparePrice?.toPrice(),
        updatedAt = updatedAt ?: "",
        optionGroups = optionGroups?.data?.map { it.toProductOptionGroup(id ?: "") } ?: emptyList(),
        assets = assets?.productAssetDtos?.map { it.toProductAsset() } ?: emptyList()
    )
}

fun ProductOptionGroupDataDto.toProductOptionGroup(productId: String): ProductOptionGroup {
    return ProductOptionGroup(
        id = id ?: "",
        productId = productId,
        name = name ?: "",
        isEnabled = isEnabled == true,
        isRequired = isRequired == true,
        minOptions = minOptions ?: 0,
        maxOptions = maxOptions ?: 0,
        options = options?.data?.map { it.toProductOption(name ?: "") } ?: emptyList()
    )
}

fun ProductOptionDataDto.toProductOption(optionGroupName: String): ProductOption {
    return ProductOption(
        id = id ?: "",
        name = name ?: "",
        additionalPrice = additionalPrice?.toPrice(),
        chooseMoreThanOnce = chooseMoreThanOnce == true,
        optionGroupId = optionGroupId ?: "",
        optionGroupName = optionGroupName,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        isEnabled = isEnabled == true,
        isSelectedToDisable = false
    )
}

fun ProductAssetDto.toProductAsset(): ProductAsset {
    return ProductAsset(
        createdAt = createdAt ?: "",
        imageUrl = imageUrl ?: "",
        imageType = imageType ?: "",
        id = id ?: "",
        productId = productId ?: "",
        resourceType = resourceType ?: "",
        updatedAt = updatedAt ?: ""
    )
}
