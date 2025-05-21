package de.tarent.crud.adapters.rest.routes

import com.zaxxer.hikari.HikariDataSource
import de.tarent.crud.adapters.rest.routes.HealthStatus.DOWN
import de.tarent.crud.adapters.rest.routes.HealthStatus.UP
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable

enum class HealthStatus {
    UP,
    DOWN,
}

@Serializable
data class Health(
    val httpServer: HealthStatus,
    val database: HealthStatus,
)

fun Route.adminPage(database: HikariDataSource) {
    val logger = KotlinLogging.logger { }

    //val database: HikariDataSource by inject()

    get("/admin/status") {
        call.respond(HttpStatusCode.OK, """{ "status": "UP" }""")
    }

    get("/admin/health") {
        val state = try {
            val dbState = if (database.isRunning) UP else DOWN
            Health(UP, dbState)
        } catch (e: Exception) {
            logger.error(e) { "Error while checking system status" }
            Health(DOWN, DOWN)
        }
        call.respond(HttpStatusCode.OK, state)
    }
}
