package ch.healthwallet.util

import kotlinx.datetime.*

fun createTimestampWithTimeZone(timeZoneId: String): String {
    // Get the current UTC time
    val instant = Clock.System.now()

    // Get the TimeZone object from the given ID
    val timeZone = TimeZone.of(timeZoneId)

    // Convert the instant to local date and time based on the time zone
    val localDateTime = instant.toLocalDateTime(timeZone)

    // Format date and time separately to avoid fractional seconds
    val date = localDateTime.date
    val time = localDateTime.time

    // Format time to exclude nanoseconds
    val formattedTime = time.hour.toString().padStart(2, '0') +
            ":${time.minute.toString().padStart(2, '0')}" +
            ":${time.second.toString().padStart(2, '0')}"

    // Get the offset in hours and minutes
    val offset = timeZone.offsetAt(instant)
    val offsetHours = offset.totalSeconds / 3600
    val offsetMinutes = (offset.totalSeconds % 3600) / 60

    // Create a multiplatform-compatible offset string
    val formattedOffset = formatOffset(offsetHours, offsetMinutes)

    return "${date}T$formattedTime$formattedOffset"
}

fun formatOffset(hours: Int, minutes: Int): String {
    val hoursPrefix = if (hours >= 0) "+" else "-"
    val absHours = kotlin.math.abs(hours)
    val formattedHours = absHours.toString().padStart(2, '0')
    val formattedMinutes = minutes.toString().padStart(2, '0')
    return "$hoursPrefix$formattedHours:$formattedMinutes"
}

fun main() {
    val timestampCET = createTimestampWithTimeZone("Europe/Zurich")
    println("Timestamp (CET): $timestampCET")
}
