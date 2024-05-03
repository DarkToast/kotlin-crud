package de.tarent.crud

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.tarent.crud.application.DeviceService
import de.tarent.crud.application.GroupService
import de.tarent.crud.application.MetricService
import de.tarent.crud.adapters.database.DeviceEntity
import de.tarent.crud.adapters.database.DeviceRepository
import de.tarent.crud.adapters.database.GroupEntity
import de.tarent.crud.adapters.database.GroupRepository
import de.tarent.crud.adapters.database.MetricEntity
import de.tarent.crud.adapters.database.MetricRepository
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dependencies = { appConfiguration: ApplicationConfig ->
    module {
        singleOf(::GroupService)
        singleOf(::GroupRepository)
        singleOf(::DeviceService)
        singleOf(::DeviceRepository)
        singleOf(::MetricService)
        singleOf(::MetricRepository)

        single<Configuration> { Configuration.load(appConfiguration) }

        single<HikariDataSource> {
            val c: Configuration by inject()

            val config =
                HikariConfig().apply {
                    jdbcUrl = c.databaseConfig.connection
                    username = c.databaseConfig.username
                    password = c.databaseConfig.password
                    driverClassName = c.databaseConfig.driver
                    connectionTestQuery = "SELECT 1"
                    maximumPoolSize = 3
                    minimumIdle = 1
                }

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
