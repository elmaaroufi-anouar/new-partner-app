package com.done.partner.domain.services.print

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

fun Double.getTimeAfterMinutes(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MINUTE, this.toInt())
    val formatter = SimpleDateFormat("HH:mm", Locale.US)
    return formatter.format(calendar.time)
}

fun Long.formatDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    val date = Date(this)
    return dateFormat.format(date)
}