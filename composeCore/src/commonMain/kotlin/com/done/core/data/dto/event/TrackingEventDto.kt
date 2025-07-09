package com.done.core.data.dto.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackingEventDto (
    @SerialName("name") val eventName: String,
    @SerialName("@timestamp") val timeStamp: Long,
    @SerialName("payload") val payload: Map<String, String?>
)