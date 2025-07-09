package com.done.partner.data.dto.store

import com.done.partner.data.dto.product.toProduct
import com.done.partner.domain.models.orders.StoreSection
import com.done.partner.domain.models.store.Store
import com.done.partner.domain.models.store.StoreBrand

fun StoreDto.toStore(): Store {
    return Store(
        id = id ?: "",
        type = type ?: "",
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        businessEmail = businessEmail ?: "",
        invoiceEmail = invoiceEmail ?: "",
        disabledAt = disabledAt,
        iceFilePath = iceFilePath ?: "",
        ribFilePath = ribFilePath ?: "",
        longitude = longitude ?: "",
        latitude = latitude ?: "",
        storeBrand = brand?.storeBrandDataDto?.toStoreBrand(),
        storeSections = sections?.data?.map { it.toSection() } ?: emptyList(),
    )
}

fun StoreSectionDataDto.toSection(): StoreSection {
    return StoreSection(
        id = id ?: "",
        name = name ?: "",
        sortOrder = sortOrder ?: 0,
        storeId = storeId ?: "",
        products = products?.products?.map { it.toProduct() } ?: emptyList(),
        layout = layout ?: ""
    )
}

fun StoreBrandDataDto.toStoreBrand(): StoreBrand {
    return StoreBrand(
        id = id ?: "",
        name = name ?: "",
        description = description ?: "",
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: ""
    )
}