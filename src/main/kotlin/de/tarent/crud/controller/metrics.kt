package de.tarent.crud.controller

import de.tarent.crud.dtos.Metric
import de.tarent.crud.service.MetricService
import de.tarent.crud.service.results.DeviceDontExists
import de.tarent.crud.service.results.GroupDontExists
import de.tarent.crud.service.results.Ok
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import mu.KotlinLogging

fun Route.metricsPage(metricService: MetricService) {
    val logger = KotlinLogging.logger {}

    route("/groups/{groupName?}/devices/{deviceName?}/metrics") {
        post {
            val groupName = call.path("groupName") ?: return@post
            val deviceName = call.path("deviceName") ?: return@post
            val metric: Metric = call.body() ?: return@post
            logger.info { "POST new metric on device '$deviceName' of group '$groupName'" }

            when (val result = metricService.create(groupName, deviceName, metric)) {
                is GroupDontExists -> groupDontExists(call, result)
                is DeviceDontExists -> deviceDontExist(call, result)
                is Ok -> call.respond(Created, metric.withLinks(groupName, deviceName))
            }
        }
    }

}