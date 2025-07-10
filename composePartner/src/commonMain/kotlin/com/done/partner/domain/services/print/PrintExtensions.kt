package com.done.partner.domain.services.print

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun String.wrapText(maxCharsPerLine: Int = 32): List<String> {
    val words = this.split(" ")
    val lines = mutableListOf<String>()
    var currentLine = StringBuilder()

    for (word in words) {
        if (currentLine.length + word.length + 1 > maxCharsPerLine) {
            lines.add(currentLine.toString().trim())
            currentLine = StringBuilder()
        }
        currentLine.append("$word ")
    }
    if (currentLine.isNotEmpty()) {
        lines.add(currentLine.toString().trim())
    }
    return lines
}

@OptIn(ExperimentalTime::class)
fun Double.getTimeAfterMinutes(): String {
    val now = Clock.System.now()
    val future = now.plus(this.toLong().minutes)
    val local = future.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"
}

@OptIn(ExperimentalTime::class)
fun Long.formatDate(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${local.year.toString().padStart(4, '0')}-${local.monthNumber.toString().padStart(2, '0')}-${local.dayOfMonth.toString().padStart(2, '0')} " +
            "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"
}