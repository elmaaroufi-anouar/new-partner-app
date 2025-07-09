package com.done.partner.data.dto.product

import com.done.partner.data.dto.price.PriceDto
import com.done.partner.data.dto.promotion.PromotionDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDataDto(
    @SerialName("average_preparation_time") val averagePreparationTime: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("disabled_at") val disabledAt: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("number_of_calories") val numberOfCalories: Int? = null,
    @SerialName("price") val price: PriceDto? = null,
    @SerialName("compare_price") val comparePrice: PriceDto? = null,
    @SerialName("resource_type") val resourceType: String? = null,
    @SerialName("store_id") val storeId: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("assets") val assets: ProductAssetsDto? = null,
    @SerialName("option_groups") val optionGroups: ProductOptionGroupDto? = null,
    @SerialName("ingredients") val ingredients: ProductIngredientDto? = null,
    @SerialName("promotion") val promotion: PromotionDto? = null,
    @SerialName("sub_categories") val subCategories: ProductSubCategoryDto? = null,
)