package com.done.core.data.dto.event

import com.done.core.domain.models.event.TrackingEvent

fun TrackingEvent.toTrackingEventDto(): TrackingEventDto {
    return TrackingEventDto(
        eventName = eventName,
        timeStamp = timeStamp,
        payload = payload
    )
}