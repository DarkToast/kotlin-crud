package de.tarent.crud

import de.tarent.crud.persistance.Repository
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.netty.EngineMain
import org.jetbrains.exposed.sql.Database
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun main(args: Array<String>) = EngineMain.main(args)

val serviceModule = { configuration: ApplicationConfig ->
    module {
        singleOf(::GroupService)
        singleOf(::Repository)

        single { Configuration.load(configuration) }

        single {
            val c: Configuration by inject()
            val databaseConfig = c.database ?: throw IllegalStateException("No database configuration given!")
            Database.connect(
                url = databaseConfig.connection,
                driver = databaseConfig.driver,
                user = databaseConfig.username,
                password = databaseConfig.password
            )
        }
    }
}
