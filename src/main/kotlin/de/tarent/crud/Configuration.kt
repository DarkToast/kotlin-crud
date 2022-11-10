package de.tarent.crud

import io.ktor.server.config.ApplicationConfig
import mu.KotlinLogging

data class Database(
    val connection: String,
    val driver: String,
    val username: String,
    val password: String
)

data class Configuration(val database: Database) {
    companion object {
        private val logger = KotlinLogging.logger {}

        fun load(configuration: ApplicationConfig): Configuration {
            logger.info { "Loading database configuration with: " }
            val dbConfig = configuration.config("database")

            val database = Database(
                dbConfig.property("connection").getString(),
                dbConfig.property("driver").getString(),
                dbConfig.property("username").getString(),
                dbConfig.property("password").getString()
            )

            logger.info { "  Connection '${database.connection}'" }
            logger.info { "  Driver '${database.driver}'" }

            return Configuration(database)
        }
    }
}