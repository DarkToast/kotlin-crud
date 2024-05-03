package de.tarent.crud.adapters.rest.routes

import de.tarent.crud.application.DeviceService
import de.tarent.crud.application.results.DeviceAlreadyExists
import de.tarent.crud.application.results.DeviceDontExists
import de.tarent.crud.application.results.GroupDontExists
import de.tarent.crud.application.results.Ok
import de.tarent.crud.domain.Device
import de.tarent.crud.adapters.rest.body
import de.tarent.crud.adapters.rest.deviceDontExist
import de.tarent.crud.adapters.rest.dtos.CreateUpdateDeviceRequest
import de.tarent.crud.adapters.rest.dtos.Failure
import de.tarent.crud.adapters.rest.path
import io.github.oshai.kotlinlogging.KotlinLogging
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

val logger = KotlinLogging.logger("de.tarent.crud.controller.devicesKt")

fun Route.devicePage(deviceService: DeviceService) {
    route("/groups/{groupName?}/devices") {
        get {
            val groupName = call.path("groupName") ?: return@get

            logger.info { "READ list of devices for group $groupName" }
            when (val result = deviceService.listDevices(groupName)) {
                is Ok -> {
                    logger.debug { "${result.value.size}' devices loaded of group '$groupName'." }
                    val response: List<Device> =
                        result.value.map {
                            it.withLinks(groupName)
                        }
                    call.respond(OK, response)
                }

                is GroupDontExists -> groupDontExist(call, result)
            }
        }

        get("{deviceName?}") {
            val groupName = call.path("groupName") ?: return@get
            val deviceName = call.path("deviceName") ?: return@get

            logger.info { "READ device '$deviceName' for group '$groupName'." }
            when (val result = deviceService.read(groupName, deviceName)) {
                is Ok -> {
                    logger.debug { "Device '${result.value.name}' loaded" }
                    call.respond(OK, result.value.withLinks(groupName))
                }

                is GroupDontExists -> groupDontExist(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
            }
        }

        post {
            val groupName = call.path("groupName") ?: return@post
            val command =
                call.body<CreateUpdateDeviceRequest> { msg, cause ->
                    Failure.onGroup(400, msg, cause, groupName)
                } ?: return@post
            val device =
                Device(
                    name = command.name,
                    description = command.description,
                    type = command.type,
                )

            logger.info { "CREATE a new device in the group $groupName" }

            when (val result = deviceService.create(groupName, device)) {
                is Ok -> {
                    logger.debug { "Device '${result.value.name}' created" }
                    call.respond(Created, result.value.withLinks(groupName))
                }

                is GroupDontExists -> groupDontExist(call, result)
                is DeviceAlreadyExists -> deviceAlreadyExists(call, result)
            }
        }

        put("{deviceName?}") {
            val groupName: String = call.path("groupName") ?: return@put
            val deviceName: String = call.path("deviceName") ?: return@put
            val command =
                call.body<CreateUpdateDeviceRequest> { msg, cause ->
                    Failure.onGroup(400, msg, cause, groupName)
                } ?: return@put
            val device =
                Device(
                    name = command.name,
                    description = command.description,
                    type = command.type,
                )

            logger.info { "UPDATE device '$deviceName' for group '$groupName'." }

            when (val result = deviceService.update(groupName, deviceName, device)) {
                is Ok -> {
                    logger.debug { "Device '$deviceName' updated" }
                    call.respond(OK, result.value.withLinks(groupName))
                }

                is DeviceAlreadyExists -> deviceAlreadyExists(call, result)
                is GroupDontExists -> groupDontExist(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
            }
        }

        delete("{deviceName?}") {
            val groupName: String = call.path("groupName") ?: return@delete
            val deviceName: String = call.path("deviceName") ?: return@delete

            logger.info { "DELETE device '$deviceName' for group '$groupName'." }

            when (val result = deviceService.delete(groupName, deviceName)) {
                is Ok -> {
                    logger.debug { "Device '$deviceName' deleted" }
                    call.respond(OK, result.value.withLinks())
                }

                is GroupDontExists -> groupDontExist(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
            }
        }
    }
}

private suspend fun deviceAlreadyExists(
    call: ApplicationCall,
    result: DeviceAlreadyExists<*>,
) {
    val msg = "Device '${result.deviceName}' of group '${result.groupName}' already exists."
    logger.warn { msg }
    call.respond(Conflict, Failure.onGroup(409, msg, "", result.groupName))
}

private suspend fun groupDontExist(
    call: ApplicationCall,
    result: GroupDontExists<*>,
) {
    val msg = "Group ${result.groupName} was not found!"
    logger.warn { msg }
    call.respond(NotFound, Failure.onGroup(404, msg, "", result.groupName))
}
