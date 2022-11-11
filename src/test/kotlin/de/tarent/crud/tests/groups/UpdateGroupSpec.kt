package de.tarent.crud.tests.groups

import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.contentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateGroupSpec : BaseGroupSpec() {

    @Test
    fun `Update an existing group`() = Spec().componentSpec {
        // given: The default group
        createGroup(this, DEFAULT_GROUP_NAME, "Hauswirtschaftsraum")

        // when: We update the group
        val response = client.put("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(groupJson(DEFAULT_GROUP_NAME, "New description"))
        }

        // Then: The status is ok
        assertEquals(OK, response.status)

        // and: The returned group is the updated
        assertGroup(DEFAULT_GROUP_NAME, "New description", response)
    }

    @Test
    fun `Overwrite an existing group id`() = Spec().componentSpec {
        // given: The default group
        createGroup(this, DEFAULT_GROUP_NAME, "Hauswirtschaftsraum")

        // when: We update the group
        var response = client.put("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(groupJson("NEW_ID", "New description"))
        }

        // Then: The status is ok
        assertEquals(OK, response.status)

        // and: The response is the request body
        assertGroup("NEW_ID", "New description", response)

        // when: We get the group
        response = client.get("/groups/NEW_ID") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: The status is ok and the group is returned
        assertEquals(OK, response.status)
    }

    @Test
    fun `Failed - group unknown`() = Spec().componentSpec {
        // when: An unknown group is updated
        val response = client.put("/groups/UNKNOWN") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(groupJson(DEFAULT_GROUP_NAME, "New description"))
        }

        // then: not found is returned
        assertEquals(NotFound, response.status)
    }

    @Test
    fun `Failed - new id conflicts`() = Spec().componentSpec {
        // given: The default group
        createGroup(this, DEFAULT_GROUP_NAME, "Hauswirtschaftsraum")

        // and: another group
        var response = client.post("/groups") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(groupJson("OTHER_GROUP", "New description"))
        }

        assertEquals(Created, response.status)

        // when: We change the ID of the other group to the first group
        response = client.put("/groups/OTHER_GROUP") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(groupJson(DEFAULT_GROUP_NAME, "New description"))
        }

        // then: we get a conflict
        assertEquals(Conflict, response.status)
    }
}