package de.tarent.crud.adapters

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount

fun parseDateTime(value: String): LocalDateTime {
    fun LocalDateTime.transform(
        op: String,
        amount: String,
        unit: String,
    ): LocalDateTime {
        val transform =
            if (op.lowercase() == "-") {
                { tu: TemporalAmount -> this.minus(tu) }
            } else {
                { tu: TemporalAmount -> this.plus(tu) }
            }

        val duration: Duration =
            if (unit == "D") {
                Duration.parse("P${amount}D")
            } else {
                Duration.parse("PT${amount}$unit")
            }

        return transform(duration)
    }

    val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)

    // eg. now; now-4d; now+5m
    val periodPattern = "^now(([+-])(\\d{0,2})([dhms]))?\$".toRegex()
    // eg. 2022-12-29T15:30:18
    val dateTimePattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$".toRegex()

    val periodResult = periodPattern.find(value)
    if (periodResult != null) {
        return if (periodResult.value == "now") {
            now
        } else {
            val op = periodResult.groupValues[2].lowercase()
            val amount = periodResult.groupValues[3].lowercase()
            val unit = periodResult.groupValues[4].uppercase()
            now.transform(op, amount, unit)
        }
    }

    val dateTimeResult = dateTimePattern.find(value)
    if (dateTimeResult != null) {
        return LocalDateTime.parse(dateTimeResult.value)
    }

    throw IllegalArgumentException("'$value' could not be parsed.")
}