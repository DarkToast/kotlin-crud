package de.tarent.crud

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.tarent.crud.controller.groupPage
import de.tarent.crud.controller.indexPage
import de.tarent.crud.persistance.Groups
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.jetbrains.exposed.sql.Database as ExposedDatabase

abstract class BaseComponentSpec {
    fun componentTest(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            install(Koin) {
                slf4jLogger(Level.DEBUG)
                modules(serviceModule("/test-configuration.yml"))
            }

            install(ContentNegotiation) {
                jackson {
                    configure(SerializationFeature.INDENT_OUTPUT, true)
                    setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                        indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                        indentObjectsWith(DefaultIndenter("  ", "\n"))
                    })
                    registerModule(KotlinModule.Builder().build())
                }
            }

            val groupService: GroupService by inject()


            routing {
                indexPage()
                groupPage(groupService)
            }
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