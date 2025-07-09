package com.done.partner.domain.repositories.prodcuts

import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.Result
import com.done.partner.domain.models.product.Product
import com.done.partner.domain.models.product.product_option.ProductOption

interface ProductsRepository {
    suspend fun updateOptions(
        storeId: String,
        productId: String,
        options: List<ProductOption>
    ): Result<Unit, NetworkError>

    suspend fun getProduct(
        storeId: String, productId: String
    ): Result<Product, NetworkError>

    suspend fun getProducts(
        storeId: String, page: Int, query: String
    ): Result<List<Product>, NetworkError>

    suspend fun updateProductPrice(
        storeId: String, productId: String, price: Int
    ): Result<Unit, NetworkError>

    suspend fun disableProduct(
        storeId: String, productId: String
    ): Result<Unit, NetworkError>

    suspend fun enableProduct(
        storeId: String, productId: String
    ): Result<Unit, NetworkError>
}