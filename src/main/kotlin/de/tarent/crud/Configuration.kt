package de.tarent.crud

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.config.ApplicationConfig

data class DatabaseConfig(
    val connection: String,
    val driver: String,
    val username: String,
    val password: String,
)

data class Configuration(val databaseConfig: DatabaseConfig) {
    companion object {
        private val logger = KotlinLogging.logger {}

        fun load(configuration: ApplicationConfig): Configuration {
            logger.info { "Loading database configuration with: " }
            val dbProps = configuration.config("database")

            val databaseConfig =
                DatabaseConfig(
                    dbProps.property("connection").getString(),
                    dbProps.property("driver").getString(),
                    dbProps.property("username").getString(),
                    dbProps.property("password").getString(),
                )

            logger.info { "  Connection '${databaseConfig.connection}'" }
            logger.info { "  Driver '${databaseConfig.driver}'" }

            return Configuration(databaseConfig)
        }
    }
}
