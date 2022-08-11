package de.tarent.crud

import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.contentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReadGroupSpec : BaseGroupSpec() {

    @Test
    fun `GET existing group`() = componentTest {
        // given: An exiting group
        provideExistingDefaultGroup(this)

        // when: We get the default group
        val response = client.get("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: The status is ok and the group is returned
        assertEquals(OK, response.status)
        assertEquals(defaultGroupJson, response.bodyAsText())
    }

    @Test
    fun `GET group not known`() = componentTest {
        // when: We get an unknown group
        val response = client.get("/groups/NOT_KNOWN") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: It is not found
        assertEquals(NotFound, response.status)
    }
}