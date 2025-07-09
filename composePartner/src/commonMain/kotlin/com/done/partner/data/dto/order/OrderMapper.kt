package com.done.partner.data.dto.order

import com.done.partner.data.dto.customer.CustomerDataDto
import com.done.partner.data.dto.delivery.toDelivery
import com.done.partner.data.dto.driver.toDriver
import com.done.partner.data.dto.order.order_item.OrderItemDataDto
import com.done.partner.data.dto.order.order_item_option.OrderItemOptionDataDto
import com.done.partner.data.dto.order.order_item_option.OrderItemOptionDataOptionDtoDataDto
import com.done.partner.data.dto.price.toPrice
import com.done.partner.data.dto.product.toProduct
import com.done.partner.domain.models.cart.CartProduct
import com.done.partner.domain.models.cart.CartProductOption
import com.done.partner.domain.models.customer.Customer
import com.done.partner.domain.models.orders.Order
import com.done.partner.domain.models.orders.OrderItem
import com.done.partner.domain.models.orders.OrderItemOption
import com.done.partner.domain.models.product.product_option.ProductOption
import com.done.core.domain.util.utcDateToSimpleLocalDateFormat
import com.done.core.domain.util.timeSinceDate
import com.done.core.domain.util.timeUntilDate

fun OrderDto.toOrder(): Order {
    return Order(
        createdAt = createdAt?.utcDateToSimpleLocalDateFormat()?.timeSinceDate() ?: "",
        customerId = customerId ?: "",
        orderItems = orderItems?.data?.map { it.toOrderItem() } ?: emptyList(),
        deletedAt = deletedAt ?: "",
        deliveryId = deliveryId ?: "",
        inStorePickup = inStorePickup == true,
        note = note ?: "",
        id = id ?: "",
        price = price?.toPrice(),
        productAmount = productsAmount?.toPrice(),
        status = status?.value ?: "",
        storeId = storeId ?: "",
        updatedAt = updatedAt ?: "",
        vat = (price?.amount?.div(100))?.times(0.2),
        customer = customer?.customerDataDto?.toCustomer(),
        statusUpdatedAt = statusUpdatedAt ?: "",
        minutesUntilPreparation = preparationWillEndAt?.utcDateToSimpleLocalDateFormat()?.timeUntilDate(),
        preparationWillEndAt = preparationWillEndAt,
        friendlyNumber = friendlyNumber ?: "",
        deliveryEstimationInMin = deliveryEstimationInMin ?: 0.0,
        deliveryEstimationInKm = deliveryEstimationInKm ?: 0.0,
        driver = driver?.driverDataDto?.toDriver(),
        delivery = delivery?.deliveryDataDto?.toDelivery()
    )
}

fun OrderItemDataDto.toOrderItem(): OrderItem {
    return OrderItem(
        createdAt = createdAt ?: "",
        deletedAt = deletedAt ?: "",
        id = id ?: "",
        orderId = orderId ?: "",
        orderItemOptions = orderItemOptions?.data?.map { it.toOrderItemOption() } ?: emptyList(),
        price = price?.toPrice(),
        currency = price?.currency ?: "",
        productId = productId ?: "",
        quantity = quantity ?: 0,
        updatedAt = updatedAt ?: "",
        name = product?.productDataDto?.name ?: "",
        product = product?.productDataDto?.toProduct()
    )
}

fun OrderItemOptionDataDto.toOrderItemOption(): OrderItemOption {
    return OrderItemOption(
        createdAt = createdAt ?: "",
        deletedAt = deletedAt ?: "",
        id = id ?: "",
        optionId = optionId ?: "",
        orderItemId = orderItemId ?: "",
        price = price?.toPrice(),
        quantity = quantity ?: 0,
        updatedAt = updatedAt ?: "",
        name = option?.orderItemOptionDataOptionDtoDataDto?.name ?: "",
        optionGroupId = optionGroupId ?: "",
        option = option?.orderItemOptionDataOptionDtoDataDto?.toProductOption(
            option.orderItemOptionDataOptionDtoDataDto.name ?: ""
        )
    )
}

fun CustomerDataDto.toCustomer(): Customer {
    return Customer(
        email = email ?: "",
        firstName = firstName ?: "",
        id = id ?: "",
        lastName = lastName ?: "",
        phone = phone ?: ""
    )
}

fun OrderItemOptionDataOptionDtoDataDto.toProductOption(optionGroupName: String): ProductOption {
    return ProductOption(
        id = id ?: "",
        name = name ?: "",
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        additionalPrice = additionalPrice?.toPrice(),
        chooseMoreThanOnce = chooseMoreThanOnce != false,
        optionGroupId = optionGroupId ?: "",
        optionGroupName = optionGroupName,
        isEnabled = isEnabled == true,
        isSelectedToDisable = false,
    )
}

fun OrderItem.toCartProduct(): CartProduct? {
    return product?.let { product ->
        CartProduct(
            product = product,
            options = orderItemOptions.map { it.toCartProductOption() },
            quantity = quantity
        )
    }
}

fun OrderItemOption.toCartProductOption(): CartProductOption {
    return CartProductOption(
        id = optionId,
        groupId = option?.optionGroupId ?: "",
        name = name,
        quantity = quantity
    )
}