package de.tarent.crud.driver.rest

import de.tarent.crud.application.GroupService
import de.tarent.crud.application.results.GroupAlreadyExists
import de.tarent.crud.application.results.GroupDontExists
import de.tarent.crud.application.results.Ok
import de.tarent.crud.domain.Description
import de.tarent.crud.domain.Group
import de.tarent.crud.domain.Name
import de.tarent.crud.driver.rest.dtos.CreateUpdateGroupRequest
import de.tarent.crud.driver.rest.dtos.GroupResponse
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
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

    val toResponse = { group: Group -> GroupResponse.from(group).withLinks() }

    route("/groups") {
        get {
            logger.info { "READ list of groups" }
            when (val result = groupService.list()) {
                is Ok -> {
                    call.respond(OK, result.value.map(toResponse))
                }
            }
        }

        get("{name?}") {
            val name = call.path("name") ?: return@get
            logger.info { "READ group by name '$name'" }

            when (val result = groupService.read(name)) {
                is GroupDontExists -> groupDontExists(call, result)
                is Ok -> call.respond(OK, result.value.let(toResponse))
            }
        }

        post {
            val command = call.body<CreateUpdateGroupRequest>() ?: return@post
            logger.info { "CREATE group with name '${command.name}'." }
            val group = Group(
                name = Name(command.name),
                description = Description(command.description)
            )

            when (val result = groupService.create(group)) {
                is GroupAlreadyExists -> groupAlreadyExists(call, result)
                is Ok -> call.respond(Created, result.value.let(toResponse))
            }
        }

        put("{name?}") {
            val name = call.path("name") ?: return@put
            val command = call.body<CreateUpdateGroupRequest>() ?: return@put
            logger.info { "UPDATE group with name '$name'." }

            val group = Group(
                name = Name(command.name),
                description = Description(command.description)
            )

            when (val result = groupService.update(name, group)) {
                is GroupDontExists -> groupDontExists(call, result)
                is GroupAlreadyExists -> groupAlreadyExists(call, result)
                is Ok -> call.respond(OK, result.value.let(toResponse))
            }
        }

        delete("{name?}") {
            val name = call.path("name") ?: return@delete
            logger.info { "DELETE group with name '$name'." }

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
    call.respond(Conflict, Failure.onGroup(404, msg, "", result.groupName))
}
