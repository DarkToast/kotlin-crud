package de.tarent.crud

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.tarent.crud.controller.adminPage
import de.tarent.crud.controller.devicePage
import de.tarent.crud.controller.groupPage
import de.tarent.crud.controller.indexPage
import de.tarent.crud.dtos.Failure
import de.tarent.crud.service.DeviceService
import de.tarent.crud.service.GroupService
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import mu.KotlinLogging
import org.koin.core.logger.Level
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

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
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter())
            registerModule(KotlinModule.Builder().build())
        }
    }

    val groupService: GroupService by inject()
    val deviceService: DeviceService by inject()

    routing {
        indexPage()
        adminPage()
        groupPage(groupService)
        devicePage(deviceService)
    }
}