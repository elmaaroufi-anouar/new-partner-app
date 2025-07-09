package com.done.partner.data.repositories.prodcuts

import com.done.core.data.util.ApiRoutes
import com.done.partner.data.dto.pagination.toPagination
import com.done.partner.data.dto.product.ProductDataDto
import com.done.partner.data.dto.product.ProductsDto
import com.done.partner.data.dto.product.toProduct
import com.done.core.data.services.api.KtorApiService
import com.done.partner.domain.models.product.Product
import com.done.partner.domain.models.product.product_option.ProductOption
import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.NetworkErrorName
import com.done.core.domain.util.result.Result
import com.done.partner.domain.repositories.prodcuts.ProductsRepository
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlin.coroutines.cancellation.CancellationException

class ProductsRepositoryImpl(
    private val apiService: KtorApiService
) : ProductsRepository {

    override suspend fun updateOptions(
        storeId: String,
        productId: String,
        options: List<ProductOption>
    ): Result<Unit, NetworkError> {

        val groups = options.groupBy { it.optionGroupId }

        val jsonArray = buildJsonArray {
            groups.forEach { group ->
                addJsonObject {
                    put("option_group_id", group.key)
                    put("visibility", true)
                    putJsonArray("options") {
                        for (option in group.value) {
                            addJsonObject {
                                put("option_id", option.id)
                                put("visibility", option.isEnabled)
                                put("price", "${option.additionalPrice?.amount?.div(100)}")
                            }
                        }
                    }
                }
            }
        }

        val result = apiService.patch<Unit>(
            route = ApiRoutes.updateProductOptions(storeId = storeId, productId = productId),
            bodyJsonArray = jsonArray
        )

        return result
    }

    override suspend fun getProduct(
        storeId: String, productId: String
    ): Result<Product, NetworkError> {

        val result = apiService.get<ProductDataDto>(
            route = ApiRoutes.getProduct(storeId = storeId, productId = productId)
        )

        if (result is Result.Error) {
            return Result.Error(result.error)
        }

        return try {
            val products = result.data?.toProduct()
            Result.Success(data = products)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Result.Error(NetworkError(NetworkErrorName.UNKNOWN))
        }
    }

    override suspend fun getProducts(
        storeId: String, page: Int, query: String
    ): Result<List<Product>, NetworkError> {

        val result = apiService.get<ProductsDto>(
            route = ApiRoutes.getProducts(storeId = storeId, page = page, query = query)
        )

        if (result is Result.Error) {
            return Result.Error(result.error)
        }

        return try {
            val products = result.data?.products?.map { it.toProduct() }
            val pagination = result.data?.paginationMetaDto?.pagination?.toPagination()
            Result.Success(
                data = products, pagination = pagination
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Result.Error(NetworkError(NetworkErrorName.UNKNOWN))
        }
    }

    override suspend fun updateProductPrice(
        storeId: String, productId: String, price: Int
    ): Result<Unit, NetworkError> {
        val result = apiService.patch<Unit>(
            route = ApiRoutes.updateProductPrice(
                storeId = storeId, productId = productId
            ),
            body = hashMapOf("price" to "$price")
        )

        return handleResult(result)
    }

    override suspend fun disableProduct(
        storeId: String, productId: String
    ): Result<Unit, NetworkError> {
        val result = apiService.patch<Unit>(
            route = ApiRoutes.enableDisableProduct(
                storeId = storeId, productId = productId, state = "disable"
            ),
        )

        return handleResult(result)
    }

    override suspend fun enableProduct(
        storeId: String, productId: String
    ): Result<Unit, NetworkError> {
        val result = apiService.patch<Unit>(
            route = ApiRoutes.enableDisableProduct(
                storeId = storeId, productId = productId, state = "enable"
            )
        )
        return handleResult(result)
    }

    private fun handleResult(
        result: Result<Unit, NetworkError>
    ): Result<Unit, NetworkError> {

        if (result is Result.Success) {
            return Result.Success(null)
        }

        return Result.Error(result.error)
    }

}