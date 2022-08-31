package de.tarent.crud.tests

import de.tarent.crud.persistance.Groups
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.assertEquals
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

    protected suspend fun createGroup(builder: ApplicationTestBuilder, bodyContent: String): String {
        val response = builder.client.post("/groups") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(bodyContent)
        }

        assertEquals(HttpStatusCode.Created, response.status)

        return response.headers[HttpHeaders.Location]
            ?: throw IllegalStateException("Illegal creation state. No location header set!")
    }
}