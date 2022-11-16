package de.tarent.crud.controller

import de.tarent.crud.dtos.Failure
import de.tarent.crud.dtos.Group
import de.tarent.crud.service.GroupAlreadyExists
import de.tarent.crud.service.GroupDontExists
import de.tarent.crud.service.GroupService
import de.tarent.crud.service.Ok
import io.ktor.http.HttpHeaders.Location
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import mu.KotlinLogging

fun Route.groupPage(groupService: GroupService) {
    val logger = KotlinLogging.logger {}

    route("/groups") {
        get {
            logger.info { "READ list of groups" }
            when (val result = groupService.list()) {
                is Ok -> call.respond(HttpStatusCode.OK, result.value)
            }
        }

        get("{name?}") {
            val name = parameter(call, "name") ?: return@get
            logger.info { "READ group by name '$name'" }

            when (val result = groupService.read(name)) {
                is GroupDontExists -> groupDontExists(call, result)
                is Ok -> call.respond(HttpStatusCode.OK, result.value)
            }
        }

        post {
            val group = call.receive<Group>()
            logger.info { "CREATE group with name '${group.name}'." }

            when (val result = groupService.create(group)) {
                is Ok -> {
                    val response = call.response
                    response.header(Location, "/groups/${group.name}")
                    response.status(Created)
                }
                is GroupAlreadyExists -> groupAlreadyExists(call, result)
            }
        }

        put("{name?}") {
            val name = parameter(call, "name") ?: return@put

            logger.info { "UPDATE group with name '${name}'." }
            val group = call.receive<Group>()

            when (val result = groupService.update(name, group)) {
                is GroupDontExists -> groupDontExists(call, result)
                is GroupAlreadyExists -> groupAlreadyExists(call, result)
                is Ok -> call.respond(HttpStatusCode.OK, group)
            }
        }

        delete("{name?}") {
            val name: String = call.parameters["name"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, Failure(400, "Parameter name not found"))

            logger.info { "DELETE group with name '${name}'." }
            val result = groupService.delete(name)

            if (result) call.respond(HttpStatusCode.NoContent)
            else call.respond(HttpStatusCode.BadRequest, Failure(400, "Group '$name' was not found!"))
        }
    }
}

private suspend fun groupAlreadyExists(call: ApplicationCall, result: GroupAlreadyExists<*>) {
    val msg = "Group '${result.groupName}' already exists."
    logger.warn { msg }
    call.respond(Conflict, Failure(409, msg))
}

private suspend fun groupDontExists(call: ApplicationCall, result: GroupDontExists<*>) {
    val msg = "Group '${result.groupName}' does not exists."
    logger.warn { msg }
    call.respond(NotFound, Failure(404, msg))
}
