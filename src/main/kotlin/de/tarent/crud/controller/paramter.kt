package de.tarent.crud.controller

import de.tarent.crud.dtos.Failure
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.response.respond

suspend fun parameter(call: ApplicationCall, parameterName: String): String? {
    val parameter = call.parameters[parameterName]
    if(parameter == null) {
        call.respond(BadRequest, Failure(400, "Parameter '$parameterName' not found"))
    }
    return parameter
}

suspend inline fun <reified T : Any> ApplicationCall.receive(
    failure: (msg: String) -> Failure = { Failure.onIndex(400, it) }
): T? = try {
    this.receive()
} catch (e: BadRequestException) {
    val req = "${this.request.httpMethod} ${this.request.path()}"
    val msg = "Bad request body on call '$req'!"
    this.respond(BadRequest, failure(msg))
    null
}