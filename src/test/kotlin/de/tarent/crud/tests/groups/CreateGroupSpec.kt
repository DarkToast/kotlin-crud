package de.tarent.crud.tests.groups

import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders.Location
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.contentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CreateGroupSpec : BaseGroupSpec() {

    @Test
    fun `Create a group`() = componentTest {
        val response = client.post("/groups") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(groupJson)
        }

        assertEquals(Created, response.status)
        assertEquals("/groups/HWR", response.headers[Location])
    }

    @Test
    fun `Failed creation - bad request`() = componentTest {
        val response = client.post("/groups") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody("{}")
        }

        assertEquals(BadRequest, response.status)
    }

    @Test
    fun `Failed creation - existing`() = componentTest {
        provideExistingDefaultGroup(this)

        val response = client.post("/groups") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(groupJson)
        }

        assertEquals(Conflict, response.status)
    }

    private val groupJson = """
      |{
      |  "name": "HWR",
      |  "description": "Hauswirtschaftsraum"
      |}
    """.trimMargin("|")
}