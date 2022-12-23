package de.tarent.crud.controller

import de.tarent.crud.dtos.Failure
import de.tarent.crud.service.results.DeviceDontExists
import de.tarent.crud.service.results.GroupDontExists
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend fun groupDontExists(call: ApplicationCall, result: GroupDontExists<*>) {
    val msg = "Group '${result.groupName}' does not exists."
    logger.warn { msg }
    call.respond(HttpStatusCode.NotFound, Failure.onIndex(404, msg))
}

suspend fun deviceDontExist(call: ApplicationCall, result: DeviceDontExists<*>) {
    val msg = "Device '${result.deviceName}' of group '${result.groupName}' was not found!"
    logger.warn { msg }
    call.respond(HttpStatusCode.NotFound, Failure.onGroup(404, msg, "", result.groupName))
}