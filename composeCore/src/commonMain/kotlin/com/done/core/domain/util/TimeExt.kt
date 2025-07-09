package com.done.core.domain.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// Parses a string like "2024-07-04 14:30:00" into Instant
@OptIn(ExperimentalTime::class)
fun String.toInstantOrNull(timeZone: TimeZone = TimeZone.currentSystemDefault()): Instant? {
    return try {
        val localDateTime = LocalDateTime.parse(this)
        localDateTime.toInstant(timeZone)
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalTime::class)
fun String.addMinutesToDate(minutes: Int?): Long? {
    if (minutes == null) return -1L
    val instant = this.toInstantOrNull() ?: return null
    return instant.plus(minutes.minutes).toEpochMilliseconds()
}

@OptIn(ExperimentalTime::class)
fun String.dateToLong(): Long? {
    return this.toInstantOrNull()?.toEpochMilliseconds()
}

@OptIn(ExperimentalTime::class)
fun Long.toFormattedDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(timeZone)
    return localDateTime.toString() // "yyyy-MM-ddTHH:mm:ss"
        .replace("T", " ")          // -> "yyyy-MM-dd HH:mm:ss"
}

@OptIn(ExperimentalTime::class)
fun String.timeSinceDate(): String {
    val instant = this.toInstantOrNull() ?: return ""
    val now = Clock.System.now()
    val duration = now - instant
    val minutes = duration.inWholeMinutes

    return if (minutes < 60) {
        "$minutes"
    } else {
        // Format to "yyyy-MM-dd HH:mm:ss"
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        dateTime.toString().replace("T", " ")
    }
}

@OptIn(ExperimentalTime::class)
fun String.utcDateToSimpleLocalDateFormat(): String {
    return try {
        val parsedInstant = Instant.parse(this)
        val localDateTime = parsedInstant.toLocalDateTime(TimeZone.currentSystemDefault())
        localDateTime.toString().replace("T", " ")
    } catch (e: Exception) {
        ""
    }
}

@OptIn(ExperimentalTime::class)
fun String.timeUntilDate(): String? {
    return try {
        val targetInstant = this.toInstantOrNull() ?: return null
        val now = Clock.System.now()
        val diff = targetInstant - now

        if (diff <= Duration.ZERO) return "0"
        val minutes = diff.inWholeMinutes
        minutes.toString().padStart(2, '0')
    } catch (e: Exception) {
        null
    }
}