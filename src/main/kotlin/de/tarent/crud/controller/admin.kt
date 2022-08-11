package de.tarent.crud.controller

import com.zaxxer.hikari.HikariDataSource
import de.tarent.crud.controller.HealthStatus.DOWN
import de.tarent.crud.controller.HealthStatus.UP
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

enum class HealthStatus {
    UP, DOWN
}

data class Health(
    val httpServer: HealthStatus,
    val database: HealthStatus
)

fun Route.adminPage() {
    val database: HikariDataSource by inject()

    get("/admin/status") {
        call.respond(HttpStatusCode.OK, """{ "status": "UP" }""")
    }

    get("/admin/health") {
        val dbState = if (database.isRunning) UP else DOWN
        val state = Health(UP, dbState)
        call.respond(HttpStatusCode.OK, state)
    }
}
