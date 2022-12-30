package de.tarent.crud.driver.rest

import de.tarent.crud.domain.Failure
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import mu.KotlinLogging
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit.SECONDS
import java.time.temporal.TemporalAmount

suspend fun ApplicationCall.path(parameterName: String): String? {
    val parameter = this.parameters[parameterName]

    if (parameter == null) {
        val msg = "Parameter '$parameterName' not found"

        val logger = KotlinLogging.logger {}
        logger.warn(msg)

        this.respond(BadRequest, Failure(400, "Parameter '$parameterName' not found"))
    }

    return parameter
}

suspend inline fun <reified T : Any> ApplicationCall.body(
    failure: (msg: String, cause: String) -> Failure = { m, c -> Failure.onIndex(400, m, c) }
): T? {
    return try {
        this.receive()
    } catch (e: BadRequestException) {
        val req = "${this.request.httpMethod} ${this.request.path()}"
        val msg = "Bad request body on call '$req'!"

        val logger = KotlinLogging.logger {}
        logger.warn(e) { msg }

        this.respond(BadRequest, failure(msg, cause(e)))
        null
    }
}

fun cause(e: Throwable): String {
    tailrec fun step(e: Throwable): Throwable {
        val cause = e.cause
        return if (cause != null) step(cause) else e
    }

    return step(e).message ?: "n/a"
}

fun parseDateTime(value: String): LocalDateTime {
    fun LocalDateTime.transform(op: String, amount: String, unit: String): LocalDateTime {
        val transform = if (op.lowercase() == "-") {
            { tu: TemporalAmount -> this.minus(tu) }
        } else {
            { tu: TemporalAmount -> this.plus(tu) }
        }

        val duration: Duration = if (unit == "D") {
            Duration.parse("P${amount}D")
        } else {
            Duration.parse("PT${amount}$unit")
        }

        return transform(duration)
    }

    val now = LocalDateTime.now().truncatedTo(SECONDS)

    // eg. now; now-4d; now+5m
    val periodPattern = "^now(([+-])(\\d{0,6})([dhms]))?\$".toRegex()
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
