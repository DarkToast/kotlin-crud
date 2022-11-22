package de.tarent.crud.controller

import de.tarent.crud.dtos.Metric
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import mu.KotlinLogging

fun Route.metricsPage() {
    val logger = KotlinLogging.logger {}

    route("/groups/{groupName?}/devices/{deviceName?}/metrics") {
        post {
            val groupName = call.path("groupName") ?: return@post
            val deviceName = call.path("deviceName") ?: return@post
            val metric: Metric = call.body() ?: return@post

            logger.info { "POST new metric on device '$deviceName' of group '$groupName'" }
            call.respond(Created, metric.withLinks(groupName, deviceName))
        }
    }

}