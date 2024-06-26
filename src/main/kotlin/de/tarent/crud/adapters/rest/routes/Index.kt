package de.tarent.crud.adapters.rest.routes

import de.tarent.crud.adapters.rest.dtos.Index
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.indexPage() {
    get("/") {
        call.respond(HttpStatusCode.OK, Index())
    }
}
