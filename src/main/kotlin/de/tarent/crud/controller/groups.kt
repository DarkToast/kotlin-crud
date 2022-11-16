package de.tarent.crud.controller

import de.tarent.crud.dtos.Failure
import de.tarent.crud.dtos.Group
import de.tarent.crud.dtos.Method.GET
import de.tarent.crud.dtos.Method.POST
import de.tarent.crud.service.GroupAlreadyExists
import de.tarent.crud.service.GroupDontExists
import de.tarent.crud.service.GroupService
import de.tarent.crud.service.Ok
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import mu.KotlinLogging
import java.net.URI

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
                is Ok -> call.respond(Created, result.value)
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
            val name = parameter(call, "name") ?: return@delete
            logger.info { "DELETE group with name '${name}'." }

            when (val result = groupService.delete(name)) {
                is GroupDontExists -> groupDontExists(call, result)
                is Ok -> call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

private suspend fun groupAlreadyExists(call: ApplicationCall, result: GroupAlreadyExists<*>) {
    val msg = "Group '${result.groupName}' already exists."
    logger.warn { msg }
    val failure = Failure(409, msg).apply {
        addLink("get_groups", GET, URI("/groups"))
        addLink("add_group", POST, URI("/groups"))
        addLink("existing_group", GET, URI("/groups/${result.groupName}"))
    }
    call.respond(Conflict, failure)
}

private suspend fun groupDontExists(call: ApplicationCall, result: GroupDontExists<*>) {
    val msg = "Group '${result.groupName}' does not exists."
    logger.warn { msg }
    val failure = Failure(404, msg).apply {
        addLink("get_groups", GET, URI("/groups"))
        addLink("add_group", POST, URI("/groups"))
    }
    call.respond(NotFound, failure)
}
