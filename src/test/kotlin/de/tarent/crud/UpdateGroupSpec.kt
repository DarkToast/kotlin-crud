package de.tarent.crud

import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.contentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateGroupSpec : BaseGroupSpec() {

    @Test
    fun `Update an existing group`() = componentTest {
        // given: The default group
        provideExistingDefaultGroup(this)

        // when: We update the group
        val response = client.put("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(updateBody())
        }

        // Then: The status is ok
        assertEquals(OK, response.status)

        // and: The response is the request body
        assertEquals(updateBody(), response.bodyAsText())
    }

    @Test
    fun `Update an existing group id`() = componentTest {
        // given: The default group
        provideExistingDefaultGroup(this)

        // when: We update the group
        var response = client.put("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(updateBody("NEW_ID"))
        }

        // Then: The status is ok
        assertEquals(OK, response.status)

        // and: The response is the request body
        assertEquals(updateBody("NEW_ID"), response.bodyAsText())

        response = client.get("/groups/NEW_ID") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: The status is ok and the group is returned
        assertEquals(OK, response.status)
        assertEquals(updateBody("NEW_ID"), response.bodyAsText())
    }

    @Test
    fun `Failed - group unknown`() = componentTest {
        // when>
        val response = client.put("/groups/UNKNOWN") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(updateBody())
        }

        assertEquals(NotFound, response.status)
    }

    // and: new group information
    private fun updateBody(name: String = DEFAULT_GROUP_NAME) = """
          |{
          |  "name" : "$name",
          |  "description" : "New description"
          |}
        """.trimMargin("|")
}