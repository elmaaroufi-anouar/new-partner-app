package com.done.partner.domain.models.orders

import com.done.partner.domain.models.customer.Customer
import com.done.partner.domain.models.delivery.Delivery
import com.done.partner.domain.models.driver.Driver
import com.done.partner.domain.models.price.Price
import com.done.partner.domain.models.product.Product

data class Order(
    val createdAt: String,
    val customerId: String,
    val deletedAt: String,
    val deliveryId: String,
    val inStorePickup: Boolean,
    val note: String,
    val id: String,
    val price: Price?,
    val productAmount: Price?,
    val status: String,
    val storeId: String,
    val updatedAt: String,
    val statusUpdatedAt: String,
    val friendlyNumber: String,
    val deliveryEstimationInMin: Double,
    val deliveryEstimationInKm: Double,
    val minutesUntilPreparation: String?,
    val preparationWillEndAt: String?,
    val vat: Double?,
    val orderItems: List<OrderItem>,
    val customer: Customer?,
    val driver: Driver?,
    val delivery: Delivery?
)


val previewOrders = listOf(
    Order(
        createdAt = "",
        customerId = "222",
        deletedAt = "2024-12-11T09:06:26.000000Z",
        deliveryId = "2222",
        preparationWillEndAt = "",
        id = "ord_01JT162KPNB482EER7H9RSTEST",
        price = Price(
            currency = "MAD",
            display = "124 MAD",
            amount = 10.0
        ),
        status = "pending",
        storeId = "",
        note = "I don't want catchup",
        updatedAt = "",
        statusUpdatedAt = "",
        minutesUntilPreparation = "123",
        vat = 3.3,
        customer = Customer(
            email = "email@done.ma",
            firstName = "Done Customer",
            id = "some",
            lastName = "",
            phone = ""
        ),
        friendlyNumber = "123",
        deliveryEstimationInKm = 2.0,
        deliveryEstimationInMin = 10.0,
        driver = null,
        delivery = null,
        productAmount =  Price(
            currency = "MAD",
            display = "1924.00 MAD",
            amount = 10.0
        ),
        inStorePickup = true,
        orderItems = listOf(
            OrderItem(
                createdAt = "",
                deletedAt = "",
                id = "1",
                orderId = "",
                orderItemOptions = listOf(
                    OrderItemOption(
                        createdAt = "",
                        deletedAt = "",
                        id = "",
                        name = "Test option name",
                        optionId = "",
                        orderItemId = "",
                        optionGroupId = "",
                        price = Price(
                            currency = "MAD",
                            display = "14 MAD",
                            amount = 10.0
                        ),
                        quantity = 1,
                        updatedAt = "",
                        option = null
                    )
                ),
                price =  Price(
                    currency = "MAD",
                    display = "74 MAD",
                    amount = 10.0
                ),
                currency = "",
                productId = "",
                quantity = 1,
                updatedAt = "",
                name = "Test name of product",
                product = Product(
                    averagePreparationTime = 10,
                    createdAt = "",
                    description = "",
                    isEnabled = true,
                    isSelectedToDisable = false,
                    id = "1",
                    name = "product 1",
                    numberOfCalories = 23,
                    resourceType = "",
                    storeId = "",
                    updatedAt = "",
                    price = Price(
                        currency = "MAD",
                        display = "",
                        amount = 10.0
                    ),
                    comparePrice = Price(
                        currency = "MAD",
                        display = "",
                        amount = 10.0
                    ),
                    assets = emptyList(),
                    optionGroups = emptyList()
                )
            ),
            OrderItem(
                createdAt = "",
                deletedAt = "",
                id = "1",
                orderId = "",
                orderItemOptions = emptyList(),
                price =  Price(
                    currency = "MAD",
                    display = "44 MAD",
                    amount = 10.0
                ),
                currency = "",
                productId = "",
                quantity = 1,
                updatedAt = "",
                name = "Another test product",
                product = Product(
                    averagePreparationTime = 10,
                    createdAt = "",
                    description = "",
                    isEnabled = true,
                    isSelectedToDisable = false,
                    id = "3",
                    name = "product 1",
                    numberOfCalories = 23,
                    resourceType = "",
                    storeId = "",
                    updatedAt = "",
                    price = Price(
                        currency = "MAD",
                        display = "",
                        amount = 10.0
                    ),
                    comparePrice = Price(
                        currency = "MAD",
                        display = "",
                        amount = 10.0
                    ),
                    assets = emptyList(),
                    optionGroups = emptyList()
                )
            ),
        )
    )
)