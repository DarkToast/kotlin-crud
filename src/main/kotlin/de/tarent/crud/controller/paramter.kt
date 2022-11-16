package de.tarent.crud.controller

import de.tarent.crud.dtos.Failure
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend fun parameter(call: ApplicationCall, parameterName: String): String? {
    val parameter = call.parameters[parameterName]
    if(parameter == null) {
        call.respond(BadRequest, Failure(400, "Parameter '$parameterName' not found"))
    }
    return parameter
}