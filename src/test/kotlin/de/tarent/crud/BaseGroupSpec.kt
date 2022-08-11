package de.tarent.crud

import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import kotlin.test.assertEquals

abstract class BaseGroupSpec : BaseComponentSpec() {

    protected val DEFAULT_GROUP_NAME = "HWR"

    protected suspend fun provideExistingDefaultGroup(builder: ApplicationTestBuilder): String {
        val response = builder.client.post("/groups") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(defaultGroupJson)
        }

        assertEquals(Created, response.status)

        return response.headers[HttpHeaders.Location]
            ?: throw IllegalStateException("Illegal creation state. No location header set!")
    }

    protected val defaultGroupJson = """
      |{
      |  "name" : "$DEFAULT_GROUP_NAME",
      |  "description" : "Hauswirtschaftsraum"
      |}
    """.trimMargin("|")
}