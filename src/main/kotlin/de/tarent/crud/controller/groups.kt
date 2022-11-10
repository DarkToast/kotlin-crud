package de.tarent.crud.controller

import de.tarent.crud.dtos.Failure
import de.tarent.crud.dtos.Group
import de.tarent.crud.exceptionHandler
import de.tarent.crud.persistance.PeristenceException
import de.tarent.crud.service.GroupService
import io.ktor.http.HttpHeaders.Location
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Created
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
            call.respond(HttpStatusCode.OK, groupService.listGroups())
        }

        get("{name?}") {
            val name: String = call.parameters["name"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, Failure(400, "Parameter name not found"))

            logger.info { "READ group by name '$name'" }
            val group = groupService.read(name)
                ?: return@get call.respond(HttpStatusCode.NotFound, Failure(404, "Group was not found"))

            call.respond(HttpStatusCode.OK, group)
        }

        post {
            val group = call.receive<Group>()

            logger.info { "CREATE group with name '${group.name}'." }

            try {
                val result = groupService.create(group)

                if (result) {
                    val response = call.response
                    response.header(Location, "/groups/${group.name}")
                    response.status(Created)
                } else {
                    call.respond(HttpStatusCode.BadRequest, Failure(400, "Creation failed!"))
                }
            } catch (e: PeristenceException) {
                exceptionHandler(call, e)
            }
        }

        put("{name?}") {
            val name: String = call.parameters["name"]
                ?: return@put call.respond(HttpStatusCode.BadRequest, Failure(400, "Parameter name not found"))

            logger.info { "UPDATE group with name '${name}'." }
            val group = call.receive<Group>()

            try {
                val result = groupService.update(name, group)

                if (result) {
                    call.respond(HttpStatusCode.OK, group)
                } else {
                    call.respond(HttpStatusCode.BadRequest, Failure(400, "Update failed!"))
                }
            } catch(e: PeristenceException) {
                exceptionHandler(call, e)
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
