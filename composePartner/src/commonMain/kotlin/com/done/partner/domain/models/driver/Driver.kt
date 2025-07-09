package com.done.partner.domain.models.driver

data class Driver(
    val availableAt: String,
    val city: String,
    val email: String,
    val firstName: String,
    val id: String,
    val averageRating: Double,
    val isAvailable: Boolean,
    val lastName: String,
    val phone: String,
    val profileImageUrl: String
)