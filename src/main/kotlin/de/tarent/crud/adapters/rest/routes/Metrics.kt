package de.tarent.crud.adapters.rest.routes

import de.tarent.crud.adapters.rest.body
import de.tarent.crud.adapters.rest.deviceDontExist
import de.tarent.crud.adapters.rest.dtos.CreateMetricRequest
import de.tarent.crud.adapters.rest.dtos.Failure
import de.tarent.crud.adapters.rest.dtos.MetricListResponse
import de.tarent.crud.adapters.rest.dtos.Response
import de.tarent.crud.adapters.rest.groupDontExists
import de.tarent.crud.adapters.rest.parseDateTime
import de.tarent.crud.adapters.rest.path
import de.tarent.crud.application.MetricService
import de.tarent.crud.application.results.DeviceDontExists
import de.tarent.crud.application.results.GroupDontExists
import de.tarent.crud.application.results.MetricDontNotExists
import de.tarent.crud.application.results.Ok
import de.tarent.crud.domain.Metric
import de.tarent.crud.domain.MetricQuery
import io.github.oshai.kotlinlogging.KotlinLogging
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

fun Route.metricsPage(metricService: MetricService): Route {
    val logger = KotlinLogging.logger {}

    return route("/groups/{groupName?}/devices/{deviceName?}/metrics") {
        post {
            val groupName = call.path("groupName") ?: return@post
            val deviceName = call.path("deviceName") ?: return@post
            val command: CreateMetricRequest =
                call.body { msg, cause -> Failure.onDevice(400, msg, cause, groupName, deviceName) } ?: return@post

            logger.info { "POST new metric on device '$deviceName' of group '$groupName'" }
            val metric =
                Metric(
                    unit = command.unit,
                    value = command.value,
                    timestamp = command.timestamp,
                    groupName = groupName,
                    deviceName = deviceName,
                )

            when (val result = metricService.create(groupName, deviceName, metric)) {
                is GroupDontExists -> groupDontExists(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
                is Ok -> call.respond(Created, Response(result.value))
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
                    call.respond(OK, MetricListResponse(result.value))
                }
            }
        }

        get("/{metricId}") {
            val groupName = call.path("groupName") ?: return@get
            val deviceName = call.path("deviceName") ?: return@get
            val metricId: Int = call.path("metricId")?.toInt() ?: return@get

            logger.info { "GET metric '$metricId' on device '$deviceName' of group '$groupName'" }

            when (val result = metricService.read(groupName, deviceName, metricId)) {
                is GroupDontExists -> groupDontExists(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
                is MetricDontNotExists -> metricDontExist(call, result)
                is Ok -> call.respond(OK, Response(result.value))
            }
        }

        delete("/{metricId}") {
            val groupName = call.path("groupName") ?: return@delete
            val deviceName = call.path("deviceName") ?: return@delete
            val metricId: Int = call.path("metricId")?.toInt() ?: return@delete

            logger.info { "DELETE metric '$metricId' on device '$deviceName' of group '$groupName'" }

            when (val result = metricService.delete(groupName, deviceName, metricId)) {
                is GroupDontExists -> groupDontExists(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
                is MetricDontNotExists -> metricDontExist(call, result)
                is Ok -> call.respond(OK, Response(result.value))
            }
        }
    }
}

private fun ApplicationCall.metricQuery(): MetricQuery =
    MetricQuery(
        from = this.request.queryParameters["from"]?.let { parseDateTime(it) },
        to = this.request.queryParameters["to"]?.let { parseDateTime(it) },
        type = this.request.queryParameters["type"],
    )

private suspend fun metricDontExist(
    call: ApplicationCall,
    result: MetricDontNotExists<*>,
) {
    val msg =
        "Metric '${result.metricId}' of device '${result.deviceName}' of group '${result.groupName}' was not found!"
    logger.warn { msg }
    call.respond(HttpStatusCode.NotFound, Failure.onDevice(404, msg, "", result.groupName, result.deviceName))
}
