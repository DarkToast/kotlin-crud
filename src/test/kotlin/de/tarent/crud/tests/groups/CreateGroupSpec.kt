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
    fun `Create a group`() = Spec().componentSpec {
        val response = client.post("/groups") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(groupJson)
        }

        assertEquals(Created, response.status)
        assertEquals("/groups/HWR", response.headers[Location])
    }

    @Test
    fun `Failed creation - bad request`() = Spec().componentSpec {
        // given: an invalid json string
        val body = "{}"

        // when: the group is created
        val response = client.post("/groups") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(body)
        }

        // then: bad request is returned
        assertEquals(BadRequest, response.status)
    }

    @Test
    fun `Failed creation - existing`() = Spec().componentSpec {
        // given: An existing group
        createGroup(this, DEFAULT_GROUP_NAME, "Hauswirtschaftsraum")

        // and: A valid json body same to the existing
        val body = groupJson

        // when: the group is created
        val response = client.post("/groups") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(body)
        }

        // then: Conflict is returned
        assertEquals(Conflict, response.status)
    }

    private val groupJson = """
      |{
      |  "name": "HWR",
      |  "description": "Hauswirtschaftsraum"
      |}
    """.trimMargin("|")
}