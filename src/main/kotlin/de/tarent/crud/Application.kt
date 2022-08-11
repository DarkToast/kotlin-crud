package de.tarent.crud

import de.tarent.crud.persistance.Repository
import org.jetbrains.exposed.sql.Database
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun main() {
    startServer()
}

val serviceModule = { configurationFile: String ->
    module {
        singleOf(::GroupService)
        singleOf(::Repository)

        single { Configuration.load(configurationFile) ?: Configuration() }

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
