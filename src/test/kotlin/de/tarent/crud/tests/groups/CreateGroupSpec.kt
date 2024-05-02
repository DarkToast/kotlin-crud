package de.tarent.crud.tests.groups

import de.tarent.crud.driver.rest.dtos.Failure
import de.tarent.crud.driver.rest.dtos.GroupResponse
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.contentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CreateGroupSpec : BaseGroupSpec() {
    @Test
    fun `Create a group`() =
        Spec().componentSpec {
            // when: We create a new group
            val response =
                client.post("/groups") {
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                    setBody(groupJson)
                }

            // then: Status created in response
            assertThat(response.status).isEqualTo(Created)

            // and: The group as body
            assertGroup("HWR", "Hauswirtschaftsraum", response)

            // and: with further links
            val group: GroupResponse = json.decodeFromString(response.bodyAsText())
            assertLink("index", "/", "GET", group.links)
            assertLink("_self", "/groups/HWR", "GET", group.links)
            assertLink("delete", "/groups/HWR", "DELETE", group.links)
            assertLink("update", "/groups/HWR", "PUT", group.links)
            assertLink("add_device", "/groups/HWR/devices", "POST", group.links)
            assertLink("list_devices", "/groups/HWR/devices", "GET", group.links)
        }

    @Test
    fun `Failed creation - bad request`() =
        Spec().componentSpec {
            // given: an invalid json string
            val body = "{}"

            // when: the group is created
            val response =
                client.post("/groups") {
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                    setBody(body)
                }

            // then: bad request is returned
            assertThat(response.status).isEqualTo(BadRequest)
            val failure: Failure = json.decodeFromString(response.bodyAsText())
            assertLink("index", "/", "GET", failure.links)
            assertLink("get_groups", "/groups", "GET", failure.links)
        }

    @Test
    fun `Failed creation - existing`() =
        Spec().componentSpec {
            // given: An existing group
            createGroup(this, DEFAULT_GROUP_NAME, "Hauswirtschaftsraum")

            // and: A valid json body same to the existing
            val body = groupJson

            // when: the group is created
            val response =
                client.post("/groups") {
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                    setBody(body)
                }

            // then: Conflict is returned
            assertThat(response.status).isEqualTo(Conflict)

            // and: It has further links
            val failure: Failure = json.decodeFromString(response.bodyAsText())
            assertLink("index", "/", "GET", failure.links)
            assertLink("get_groups", "/groups", "GET", failure.links)
            assertLink("get_group", "/groups/$DEFAULT_GROUP_NAME", "GET", failure.links)
        }

    private val groupJson =
        """
      |{
      |  "name": "HWR",
      |  "description": "Hauswirtschaftsraum"
      |}
    """.trimMargin("|")
}
