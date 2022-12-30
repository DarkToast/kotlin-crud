package de.tarent.crud

import de.tarent.crud.controller.adminPage
import de.tarent.crud.controller.devicePage
import de.tarent.crud.controller.groupPage
import de.tarent.crud.controller.indexPage
import de.tarent.crud.controller.metricsPage
import de.tarent.crud.dtos.Failure
import de.tarent.crud.service.DeviceService
import de.tarent.crud.service.GroupService
import de.tarent.crud.service.MetricService
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import mu.KotlinLogging
import org.koin.core.logger.Level
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.server() {
    val logger = KotlinLogging.logger {}

    install(StatusPages) {
        exception<Throwable> { call, e ->
            logger.error(e) { "Got an unknown exception! Measure error!" }
            call.respond(
                InternalServerError,
                Failure(InternalServerError.value, e.message ?: "Unexpected error")
            )
        }
    }

    install(Koin) {
        slf4jLogger(Level.DEBUG)
        modules(serviceModule(environment.config))
    }

    install(ContentNegotiation) {
        json()
    }

    val groupService: GroupService by inject()
    val deviceService: DeviceService by inject()
    val metricService: MetricService by inject()

    routing {
        indexPage()
        adminPage()
        groupPage(groupService)
        devicePage(deviceService)
        metricsPage(metricService)
    }
}
