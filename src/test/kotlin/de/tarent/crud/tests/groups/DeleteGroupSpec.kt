package de.tarent.crud.tests.groups

import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.contentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeleteGroupSpec : BaseGroupSpec() {

    @Test
    fun `Delete group`() = componentTest {
        // given: An exiting group
        createGroup(this, DEFAULT_GROUP_NAME, "Hauswirtschaftsraum")

        // when: the group is deleted
        var response = client.delete("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: return is no content
        assertEquals(NoContent, response.status)

        // when: The deleted group is received
        response = client.get("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: it is not found
        assertEquals(NotFound, response.status)
    }

    @Test
    fun `Delete unknown group - not found`() = componentTest {
        // when: an unknown group is received
        val response = client.get("/groups/UNKNOWN") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: it is not found
        assertEquals(NotFound, response.status)
    }
}