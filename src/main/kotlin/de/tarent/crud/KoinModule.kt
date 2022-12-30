package de.tarent.crud

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.tarent.crud.driven.database.DeviceEntity
import de.tarent.crud.driven.database.DeviceRepository
import de.tarent.crud.driven.database.GroupEntity
import de.tarent.crud.driven.database.GroupRepository
import de.tarent.crud.driven.database.MetricEntity
import de.tarent.crud.driven.database.MetricRepository
import de.tarent.crud.application.DeviceService
import de.tarent.crud.application.GroupService
import de.tarent.crud.application.MetricService
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@Suppress("RemoveExplicitTypeArguments")
val serviceModule = { configuration: ApplicationConfig ->
    module {
        singleOf(::GroupService)
        singleOf(::GroupRepository)
        singleOf(::DeviceRepository)
        singleOf(::DeviceService)
        singleOf(::MetricService)
        singleOf(::MetricRepository)

        single<Configuration> { Configuration.load(configuration) }

        single<HikariDataSource> {
            val c: Configuration by inject()

            val config = HikariConfig()
            config.jdbcUrl = c.databaseConfig.connection
            config.username = c.databaseConfig.username
            config.password = c.databaseConfig.password
            config.driverClassName = c.databaseConfig.driver
            config.connectionTestQuery = "SELECT 1"
            config.maximumPoolSize = 3
            config.minimumIdle = 1

            HikariDataSource(config)
        }

        single<Database> {
            val source: HikariDataSource by inject()
            val database = Database.connect(source)

            transaction(database) {
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(GroupEntity, DeviceEntity, MetricEntity)
            }

            database
        }
    }
}
