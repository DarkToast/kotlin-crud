package de.tarent.crud.tests.groups

import de.tarent.crud.dtos.Failure
import de.tarent.crud.dtos.Group
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.contentType
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReadGroupSpec : BaseGroupSpec() {

    @Test
    fun `GET a list of groups`() = Spec().componentSpec {
        // given: Two exiting groups
        createGroup(this, "group1", "first_group")
        createGroup(this, "group2", "second_group")

        // when: We get all groups
        val response = client.get("/groups") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: Status is OK
        assertEquals(OK, response.status)
        val body = response.bodyAsText()
        val list: List<Group> = json.decodeFromString(ListSerializer(Group.serializer()), body)

        // then: A list is returned
        assertEquals(2, list.size)
        assertGroup("group1", "first_group", list[0])
        assertGroup("group2", "second_group", list[1])
    }

    @Test
    fun `GET existing group`() = Spec().componentSpec {
        // given: An exiting group
        createGroup(this, DEFAULT_GROUP_NAME, "Hauswirtschaftsraum")

        // when: We get the default group
        val response = client.get("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: The status is ok
        assertEquals(OK, response.status)

        // and: the group is returned
        assertGroup(DEFAULT_GROUP_NAME, "Hauswirtschaftsraum", response)

        // and: It has all further links
        val group: Group = json.decodeFromString(response.bodyAsText())
        assertLink("_self", "/groups/$DEFAULT_GROUP_NAME", "GET", group.links)
        assertLink("delete", "/groups/$DEFAULT_GROUP_NAME", "DELETE", group.links)
        assertLink("update", "/groups/$DEFAULT_GROUP_NAME", "PUT", group.links)
        assertLink("add_device", "/groups/$DEFAULT_GROUP_NAME", "POST", group.links)
        assertLink("list_devices", "/groups/$DEFAULT_GROUP_NAME/devices", "GET", group.links)
    }

    @Test
    fun `GET group not known`() = Spec().componentSpec {
        // when: We get an unknown group
        val response = client.get("/groups/NOT_KNOWN") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: It is not found
        assertEquals(NotFound, response.status)

        // and: It has further links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("get_groups", "/groups", "GET", failure.links)
        assertLink("add_group", "/groups", "POST", failure.links)
    }
}