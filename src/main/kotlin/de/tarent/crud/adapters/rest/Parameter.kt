package de.tarent.crud.adapters.rest

import de.tarent.crud.adapters.rest.dtos.Failure
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*

suspend fun ApplicationCall.path(parameterName: String): String? {
    val parameter = this.parameters[parameterName]

    if (parameter == null) {
        val msg = "Parameter '$parameterName' not found"

        val logger = KotlinLogging.logger {}
        logger.warn { msg }

        this.respond(BadRequest, Failure(400, "Parameter '$parameterName' not found"))
    }

    return parameter
}

suspend inline fun <reified T : Any> ApplicationCall.body(
    failure: (msg: String, cause: String) -> Failure = { m, c -> Failure.onIndex(400, m, c) },
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

