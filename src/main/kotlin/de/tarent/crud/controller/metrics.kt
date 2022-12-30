package de.tarent.crud.controller

import de.tarent.crud.dtos.Failure
import de.tarent.crud.dtos.Metric
import de.tarent.crud.dtos.MetricQuery
import de.tarent.crud.service.MetricService
import de.tarent.crud.service.results.DeviceDontExists
import de.tarent.crud.service.results.GroupDontExists
import de.tarent.crud.service.results.MetricDontNotExists
import de.tarent.crud.service.results.Ok
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import mu.KotlinLogging
import java.util.UUID

fun Route.metricsPage(metricService: MetricService) {
    val logger = KotlinLogging.logger {}

    route("/groups/{groupName?}/devices/{deviceName?}/metrics") {
        post {
            val groupName = call.path("groupName") ?: return@post
            val deviceName = call.path("deviceName") ?: return@post
            val metric: Metric = call.body { msg, cause -> Failure.onDevice(400, msg, cause, groupName, deviceName) } ?: return@post

            logger.info { "POST new metric on device '$deviceName' of group '$groupName'" }

            when (val result = metricService.create(groupName, deviceName, metric)) {
                is GroupDontExists -> groupDontExists(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
                is Ok -> call.respond(Created, metric.withLinks(groupName, deviceName))
            }
        }

        get {
            val groupName = call.path("groupName") ?: return@get
            val deviceName = call.path("deviceName") ?: return@get
            val query = call.metricQuery()

            logger.info { "QUERY metrics on device '$deviceName' of group '$groupName'" }

            when (val result = metricService.query(groupName, deviceName, query)) {
                is GroupDontExists -> groupDontExists(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
                is Ok -> {
                    logger.info { "Found ${result.value.metrics.size} metrics" }
                    call.respond(OK, result.value.withLinks(groupName, deviceName))
                }
            }
        }

        get("/{metricId}") {
            val groupName = call.path("groupName") ?: return@get
            val deviceName = call.path("deviceName") ?: return@get
            val metricId: UUID = call.path("metricId")
                ?.let { UUID.fromString(it) }
                ?: return@get

            logger.info { "GET metric '$metricId' on device '$deviceName' of group '$groupName'" }

            when (val result = metricService.read(groupName, deviceName, metricId)) {
                is GroupDontExists -> groupDontExists(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
                is MetricDontNotExists -> metricDontExist(call, result)
                is Ok -> call.respond(OK, result.value)
            }
        }

        delete("/{metricId}") {
            val groupName = call.path("groupName") ?: return@delete
            val deviceName = call.path("deviceName") ?: return@delete
            val metricId: UUID = call.path("metricId")
                ?.let { UUID.fromString(it) }
                ?: return@delete

            logger.info { "DELETE metric '$metricId' on device '$deviceName' of group '$groupName'" }

            when (val result = metricService.delete(groupName, deviceName, metricId)) {
                is GroupDontExists -> groupDontExists(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
                is MetricDontNotExists -> metricDontExist(call, result)
                is Ok -> call.respond(OK, result.value)
            }
        }
    }
}

private fun ApplicationCall.metricQuery(): MetricQuery = MetricQuery(
    from = this.request.queryParameters["from"]?.let { parseDateTime(it) },
    to = this.request.queryParameters["to"]?.let { parseDateTime(it) },
    type = this.request.queryParameters["type"]
)

private suspend fun metricDontExist(call: ApplicationCall, result: MetricDontNotExists<*>) {
    val msg =
        "Metric '${result.metricId}' of device '${result.deviceName}' of group '${result.groupName}' was not found!"
    logger.warn { msg }
    call.respond(HttpStatusCode.NotFound, Failure.onDevice(404, msg, "", result.groupName, result.deviceName))
}
