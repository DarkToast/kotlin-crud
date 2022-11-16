package de.tarent.crud.controller

import de.tarent.crud.dtos.Index
import de.tarent.crud.dtos.Method
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import java.net.URI

fun Route.indexPage() {
    val body = Index()
        .addLink("_self", Method.GET, URI("/"))
        .addLink("get_groups", Method.GET, URI("/groups"))
        .addLink("add_group", Method.POST, URI("/groups"))

    get("/") {
        call.respond(HttpStatusCode.OK, body)
    }
}
