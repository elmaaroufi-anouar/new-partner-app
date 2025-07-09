package com.done.core.domain.models.event

data class TrackingEvent (
    val eventName: String,
    val timeStamp: Long,
    val payload: Map<String, String?>
)