package de.tarent.crud

import de.tarent.crud.persistance.Groups
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.java.KoinJavaComponent.inject
import org.jetbrains.exposed.sql.Database as ExposedDatabase

abstract class BaseComponentSpec {
    fun componentTest(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        environment {
            config = ApplicationConfig("test-application.conf")
        }

        try {
            block(this)
        } finally {
            val db: ExposedDatabase by inject(ExposedDatabase::class.java)
            transaction(db) {
                Groups.deleteAll()
            }

            stopKoin()
        }
    }
}