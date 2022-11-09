@file:Suppress("RemoveExplicitTypeArguments")

package de.tarent.crud

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import de.tarent.crud.persistance.GroupEntity
import de.tarent.crud.persistance.GroupRepository
import de.tarent.crud.service.GroupService
import io.ktor.server.netty.EngineMain
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


fun main(args: Array<String>) = EngineMain.main(args)

val serviceModule = { configuration: ApplicationConfig ->
    module {
        singleOf(::GroupService)
        singleOf(::GroupRepository)

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
                SchemaUtils.create(GroupEntity)
            }

            database
        }
    }
}
