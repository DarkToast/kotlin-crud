package de.tarent.crud

import de.tarent.crud.adapters.rest.dtos.Failure
import de.tarent.crud.adapters.rest.routes.adminPage
import de.tarent.crud.adapters.rest.routes.devicePage
import de.tarent.crud.adapters.rest.routes.groupPage
import de.tarent.crud.adapters.rest.routes.indexPage
import de.tarent.crud.adapters.rest.routes.metricsPage
import de.tarent.crud.application.DeviceService
import de.tarent.crud.application.GroupService
import de.tarent.crud.application.MetricService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.koin.core.logger.Level as KoinLevel
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level as Slf4JLevel

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.server() {
    val logger = KotlinLogging.logger {}

    install(StatusPages) {
        exception<Throwable> { call, e ->
            logger.error(e) { "Got an unknown exception! Measure error!" }
            call.respond(
                InternalServerError,
                Failure(InternalServerError.value, e.message ?: "Unexpected error"),
            )
        }
    }

    install(CallLogging) {
        level = Slf4JLevel.INFO
    }

    install(Koin) {
        slf4jLogger(KoinLevel.DEBUG)
        modules(dependencies(environment.config))
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
