package de.tarent.crud.controller

import de.tarent.crud.dtos.Device
import de.tarent.crud.dtos.Failure
import de.tarent.crud.exceptionHandler
import de.tarent.crud.persistance.PeristenceException
import de.tarent.crud.service.DeviceAlreadyExists
import de.tarent.crud.service.DeviceDontExists
import de.tarent.crud.service.DeviceService
import de.tarent.crud.service.Failed
import de.tarent.crud.service.GroupDontExists
import de.tarent.crud.service.Ok
import de.tarent.crud.service.WriteResult
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.NotFound
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

fun Route.devicePage(deviceService: DeviceService) {
    val logger = KotlinLogging.logger {}

    route("/groups/{groupName?}/devices") {
        get {
            val groupName: String = call.parameters["groupName"]
                ?: return@get call.respond(BadRequest, Failure(400, "Parameter groupName not found"))

            logger.info { "READ list of devices for group $groupName" }
            when (val result = deviceService.listDevices(groupName)) {
                is Ok -> {
                    logger.debug { "${result.value.size}' devices loaded of group '$groupName'." }
                    call.respond(HttpStatusCode.OK, result.value)
                }
                is GroupDontExists -> {
                    val msg = "Group '${result.groupName}' was not found!"
                    logger.warn { msg }
                    call.respond(NotFound, Failure(404, msg))
                }
                is Failed -> {
                    val msg = "Unknown error occurred"
                    logger.error(result.e) { msg }
                    call.respond(BadRequest, Failure(404, msg))
                }
            }
        }


        get("{deviceName?}") {
            val groupName: String = call.parameters["groupName"]
                ?: return@get call.respond(BadRequest, Failure(400, "Parameter 'groupName' not found"))

            val deviceName: String = call.parameters["deviceName"]
                ?: return@get call.respond(BadRequest, Failure(400, "Parameter 'deviceName' not found"))

            logger.info { "READ device '$deviceName' for group '$groupName'." }
            when (val result = deviceService.read(groupName, deviceName)) {
                is Ok -> {
                    logger.debug { "Device '${result.value.name}' loaded" }
                    call.respond(HttpStatusCode.OK, result.value)
                }
                is GroupDontExists -> {
                    val msg = "Group '${result.groupName}' was not found!"
                    logger.warn { msg }
                    call.respond(NotFound, Failure(404, msg))
                }
                is DeviceDontExists -> {
                    val msg = "Device '${result.deviceName}' of group '${result.groupName}' was not found!"
                    logger.warn { msg }
                    call.respond(NotFound, Failure(404, msg))
                }
            }
        }


        post {
            val device = call.receive<Device>()
            val groupName: String = call.parameters["groupName"]
                ?: return@post call.respond(BadRequest, Failure(400, "Parameter groupName not found"))

            logger.info { "CREATE a new device in the group $groupName" }

            try {
                when(val result: Result = deviceService.create(groupName, device)) {
                    is Ok -> {
                        val response = call.response
                        response.header(HttpHeaders.Location, "/groups/${result.groupName}/devices/${result.deviceName}")
                        response.status(HttpStatusCode.Created)
                        logger.debug { "Device ${result.deviceName} created" }
                    }
                    is GroupDontExists -> {
                        val msg = "Group ${result.groupName} was not found!"
                        logger.warn { msg }
                        call.respond(NotFound, Failure(404, msg))
                    }
                    is DeviceAlreadyExists -> {
                        val msg = "Device ${result.deviceName} already exists in group ${result.groupName}!"
                        logger.warn { msg }
                        call.respond(Conflict, Failure(409, msg))
                    }
                    is Failed -> {
                        val msg = "Unknown error occurred"
                        logger.error(result.e) { msg }
                        call.respond(BadRequest, Failure(404, msg))
                    }
                }
            } catch (e: PeristenceException) {
                exceptionHandler(call, e)
            }
        }


        put("{deviceName?}") {
            logger.info { "UPDATE a device in a group" }
            call.respond(HttpStatusCode.OK, "")
        }


        delete("{deviceName?}") {
            logger.info { "DELETE a device in a group" }
            call.respond(HttpStatusCode.OK, "")
        }
    }
}