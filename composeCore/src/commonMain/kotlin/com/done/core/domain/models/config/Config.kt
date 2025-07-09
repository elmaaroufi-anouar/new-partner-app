package com.done.core.domain.models.config

data class Config(
    val partnerAndroidActivateFCM: Boolean,
    val partnerAndroidPlayServicesUrl: String,
    val partnerAndroidUpdateUrl: String,
    val partnerAndroidVersion: Int,
    val partnerSendAppEvents: Boolean
)