package de.tarent.crud.controller

import de.tarent.crud.dtos.Device
import de.tarent.crud.dtos.Failure
import de.tarent.crud.exceptionHandler
import de.tarent.crud.persistance.PeristenceException
import de.tarent.crud.service.DeviceAlreadyExists
import de.tarent.crud.service.DeviceDontExists
import de.tarent.crud.service.DeviceService
import de.tarent.crud.service.GroupDontExists
import de.tarent.crud.service.Ok
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Conflict
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

val logger = KotlinLogging.logger("de.tarent.crud.controller.devicesKt")

fun Route.devicePage(deviceService: DeviceService) {
    route("/groups/{groupName?}/devices") {
        get {
            val groupName = parameter(call, "groupName") ?: return@get

            logger.info { "READ list of devices for group $groupName" }
            when (val result = deviceService.listDevices(groupName)) {
                is Ok -> {
                    logger.debug { "${result.value.size}' devices loaded of group '$groupName'." }
                    call.respond(HttpStatusCode.OK, result.value)
                }
                is GroupDontExists -> groupDontExist(call, result)
            }
        }


        get("{deviceName?}") {
            val groupName = parameter(call, "groupName") ?: return@get
            val deviceName = parameter(call, "deviceName") ?: return@get

            logger.info { "READ device '$deviceName' for group '$groupName'." }
            when (val result = deviceService.read(groupName, deviceName)) {
                is Ok -> {
                    logger.debug { "Device '${result.value.name}' loaded" }
                    call.respond(HttpStatusCode.OK, result.value)
                }
                is GroupDontExists -> groupDontExist(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
            }
        }


        post {
            val device = call.receive<Device>()
            val groupName = parameter(call, "groupName") ?: return@post

            logger.info { "CREATE a new device in the group $groupName" }

            try {
                when (val result = deviceService.create(groupName, device)) {
                    is Ok -> {
                        val response = call.response
                        response.header(
                            HttpHeaders.Location,
                            "/groups/${result.value.first}/devices/${result.value.second}"
                        )
                        response.status(HttpStatusCode.Created)
                        logger.debug { "Device '${result.value.first}' created" }
                    }
                    is GroupDontExists -> groupDontExist(call, result)
                    is DeviceAlreadyExists -> deviceAlreadyExists(call, result)
                }
            } catch (e: PeristenceException) {
                exceptionHandler(call, e)
            }
        }

        put("{deviceName?}") {
            val groupName: String = parameter(call, "groupName") ?: return@put
            val deviceName: String = parameter(call, "deviceName") ?: return@put
            val device = call.receive<Device>()

            logger.info { "UPDATE device '$deviceName' for group '$groupName'." }

            when (val result = deviceService.update(groupName, deviceName, device)) {
                is Ok -> {
                    logger.debug { "Device '$deviceName' updated" }
                    call.respond(HttpStatusCode.OK, result.value)
                }
                is DeviceAlreadyExists -> deviceAlreadyExists(call, result)
                is GroupDontExists -> groupDontExist(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
            }
        }


        delete("{deviceName?}") {
            val groupName: String = parameter(call, "groupName") ?: return@delete
            val deviceName: String = parameter(call, "deviceName") ?: return@delete

            logger.info { "DELETE device '$deviceName' for group '$groupName'." }

            when (val result = deviceService.delete(groupName, deviceName)) {
                is Ok -> {
                    logger.debug { "Device '$deviceName' deleted" }
                    call.respond(HttpStatusCode.NoContent)
                }
                is GroupDontExists -> groupDontExist(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
            }
        }
    }
}

private suspend fun deviceAlreadyExists(call: ApplicationCall, result: DeviceAlreadyExists<*>) {
    val msg = "Device '${result.deviceName}' of group '${result.groupName}' already exists."
    logger.warn { msg }
    call.respond(Conflict, Failure(409, msg))
}

private suspend fun deviceDontExist(call: ApplicationCall, result: DeviceDontExists<*>) {
    val msg = "Device '${result.deviceName}' of group '${result.groupName}' was not found!"
    logger.warn { msg }
    call.respond(NotFound, Failure(404, msg))
}

private suspend fun groupDontExist(call: ApplicationCall, result: GroupDontExists<*>) {
    val msg = "Group ${result.groupName} was not found!"
    logger.warn { msg }
    call.respond(NotFound, Failure(404, msg))
}