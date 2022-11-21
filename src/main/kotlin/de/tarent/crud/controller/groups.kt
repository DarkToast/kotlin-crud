package de.tarent.crud.controller

import de.tarent.crud.dtos.Failure
import de.tarent.crud.dtos.Group
import de.tarent.crud.dtos.Index
import de.tarent.crud.service.GroupAlreadyExists
import de.tarent.crud.service.GroupDontExists
import de.tarent.crud.service.GroupService
import de.tarent.crud.service.Ok
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
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
                is Ok -> call.respond(OK, result.value.map { it.withLinks() })
            }
        }

        get("{name?}") {
            val name = parameter(call, "name") ?: return@get
            logger.info { "READ group by name '$name'" }

            when (val result = groupService.read(name)) {
                is GroupDontExists -> groupDontExists(call, result)
                is Ok -> call.respond(OK, result.value.withLinks())
            }
        }

        post {
            val group = call.receiveFailed<Group>() ?: return@post
            logger.info { "CREATE group with name '${group.name}'." }

            when (val result = groupService.create(group)) {
                is Ok -> call.respond(Created, result.value.withLinks())
                is GroupAlreadyExists -> groupAlreadyExists(call, result)
            }
        }

        put("{name?}") {
            val name = parameter(call, "name") ?: return@put
            val group = call.receiveFailed<Group>() ?: return@put
            logger.info { "UPDATE group with name '${name}'." }

            when (val result = groupService.update(name, group)) {
                is GroupDontExists -> groupDontExists(call, result)
                is GroupAlreadyExists -> groupAlreadyExists(call, result)
                is Ok -> call.respond(OK, group.withLinks())
            }
        }

        delete("{name?}") {
            val name = parameter(call, "name") ?: return@delete
            logger.info { "DELETE group with name '${name}'." }

            when (val result = groupService.delete(name)) {
                is GroupDontExists -> groupDontExists(call, result)
                is Ok -> call.respond(OK, Index())
            }
        }
    }
}

private suspend fun groupAlreadyExists(call: ApplicationCall, result: GroupAlreadyExists<*>) {
    val msg = "Group '${result.groupName}' already exists."
    logger.warn { msg }
    call.respond(Conflict, Failure.onGroup(404, msg, result.groupName))
}

private suspend fun groupDontExists(call: ApplicationCall, result: GroupDontExists<*>) {
    val msg = "Group '${result.groupName}' does not exists."
    logger.warn { msg }
    call.respond(NotFound, Failure.onIndex(404, msg))
}
