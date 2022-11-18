package de.tarent.crud.controller

import de.tarent.crud.dtos.Index
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
