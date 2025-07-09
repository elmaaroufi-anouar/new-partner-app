package com.done.core.domain.repositories.event

interface TrackingRepository {

    suspend fun trackOpenAppEvent(
        appStartDuration: String,
        launchSuccess: String,
        errorCount: String,
        errorType: String
    )

    suspend fun trackLoginEvent(
        emailAddress: String,
        loginMethod: String
    )

    suspend fun trackLogoutEvent(
        isFromCTA: Boolean
    )

    suspend fun trackAcceptOrderEvent(
        orderCreated: String,
        acceptanceTimestamp: String,
        orderId: String,
        customerId: String,
        orderItems: Int,
        orderValue: String,
        estimatedPrepTime: String,
        specialInstructions: String,
    )

    suspend fun trackOrderIsReadyEvent(
        readyTimestamp: String,
        originalEstimatedReadyTime: String,
        restaurantId: String,
        orderId: String,
        driverId: String,
    )
}