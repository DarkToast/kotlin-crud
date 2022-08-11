package de.tarent.crud

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.tarent.crud.controller.groupPage
import de.tarent.crud.controller.indexPage
import de.tarent.crud.dtos.Failure
import de.tarent.crud.persistance.ConflictException
import de.tarent.crud.persistance.NotFoundException
import de.tarent.crud.persistance.ServiceException
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import mu.KotlinLogging
import org.koin.core.logger.Level
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger


fun startServer() {
    val logger = KotlinLogging.logger {}

    val server = embeddedServer(Netty, port = 8080) {
        install(StatusPages) {
            exception<Throwable> { call, e ->
                logger.error(e) { "Got an unknown exception! Measure error!" }
                call.respond(
                    Conflict,
                    Failure(InternalServerError.value, e.message ?: "Unexpected error")
                )
            }

            exception<ServiceException> { call, e -> exceptionHandler(call, e) }
        }

        install(Koin) {
            slf4jLogger(Level.DEBUG)
            modules(serviceModule("/configuration.yml"))
        }

        install(ContentNegotiation) {
            jackson {
                configure(SerializationFeature.INDENT_OUTPUT, true)
                setDefaultPrettyPrinter(DefaultPrettyPrinter())
                registerModule(KotlinModule.Builder().build())
            }
        }


        val groupService: GroupService by inject()

        routing {
            indexPage()
            groupPage(groupService)
        }
    }

    server.start(wait = true)
}

suspend fun exceptionHandler(call:ApplicationCall, e: ServiceException) {
    val logger = KotlinLogging.logger {}

    return when (e) {
        is ConflictException -> {
            logger.warn(e) { "Got an conflicting request!" }
            call.respond(
                Conflict,
                Failure(BadRequest.value, e.message ?: "Unexpected error")
            )
        }
        is NotFoundException -> {
            logger.warn(e) { "Requesting entity was not found!" }
            call.respond(
                NotFound,
                Failure(BadRequest.value, e.message ?: "Unexpected error")
            )
        }
    }
}