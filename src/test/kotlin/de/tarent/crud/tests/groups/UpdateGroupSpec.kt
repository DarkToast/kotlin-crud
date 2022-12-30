package de.tarent.crud.tests.groups

import de.tarent.crud.dtos.Failure
import de.tarent.crud.dtos.Group
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.contentType
import kotlinx.serialization.decodeFromString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateGroupSpec : BaseGroupSpec() {

    @Test
    fun `Update an existing group`() = spec.componentSpec {
        // when: We update the group
        val response = client.put("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(groupJson(DEFAULT_GROUP_NAME, "New description"))
        }

        // Then: The status is ok
        assertThat(response.status).isEqualTo(OK)

        // and: The returned group is the updated
        assertGroup(DEFAULT_GROUP_NAME, "New description", response)

        // and: with further links
        val group: Group = json.decodeFromString(response.bodyAsText())
        assertLink("_self", "/groups/$DEFAULT_GROUP_NAME", "GET", group.links)
        assertLink("index", "/", "GET", group.links)
        assertLink("delete", "/groups/$DEFAULT_GROUP_NAME", "DELETE", group.links)
        assertLink("update", "/groups/$DEFAULT_GROUP_NAME", "PUT", group.links)
        assertLink("add_device", "/groups/$DEFAULT_GROUP_NAME/devices", "POST", group.links)
        assertLink("list_devices", "/groups/$DEFAULT_GROUP_NAME/devices", "GET", group.links)
    }

    @Test
    fun `Overwrite an existing group id`() = spec.componentSpec {
        // when: We update the group
        var response = client.put("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(groupJson("NEW_ID", "New description"))
        }

        // Then: The status is ok
        assertThat(response.status).isEqualTo(OK)

        // and: The response is the request body
        val group: Group = json.decodeFromString(response.bodyAsText())
        assertGroup("NEW_ID", "New description", group)

        // and: with further links
        assertLink("_self", "/groups/NEW_ID", "GET", group.links)
        assertLink("index", "/", "GET", group.links)
        assertLink("delete", "/groups/NEW_ID", "DELETE", group.links)
        assertLink("update", "/groups/NEW_ID", "PUT", group.links)
        assertLink("add_device", "/groups/NEW_ID/devices", "POST", group.links)
        assertLink("list_devices", "/groups/NEW_ID/devices", "GET", group.links)

        // when: We get the group
        response = client.get("/groups/NEW_ID") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: The status is ok and the group is returned
        assertThat(response.status).isEqualTo(OK)
    }

    @Test
    fun `Failed - group unknown`() = spec.componentSpec {
        // when: An unknown group is updated
        val response = client.put("/groups/UNKNOWN") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(groupJson(DEFAULT_GROUP_NAME, "New description"))
        }

        // then: not found is returned
        assertThat(response.status).isEqualTo(NotFound)

        // and: has index links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertEquals(2, failure.links.size)
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_groups", "/groups", "GET", failure.links)
    }

    @Test
    fun `Failed - new id conflicts`() = spec.componentSpec {
        // given: another group
        createGroup(this, "OTHER_GROUP", "Hauswirtschaftsraum")

        // when: We change the ID of the other group to the first group
        val response = client.put("/groups/OTHER_GROUP") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(groupJson(DEFAULT_GROUP_NAME, "New description"))
        }

        // then: we get a conflict
        assertThat(response.status).isEqualTo(Conflict)

        // and: It has all further links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_groups", "/groups", "GET", failure.links)
        assertLink("get_group", "/groups/$DEFAULT_GROUP_NAME", "GET", failure.links)
    }
}
