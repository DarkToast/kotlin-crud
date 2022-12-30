package de.tarent.crud.tests.groups

import de.tarent.crud.domain.Index
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.contentType
import kotlinx.serialization.decodeFromString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeleteGroupSpec : BaseGroupSpec() {

    @Test
    fun `Delete group`() = spec.componentSpec {
        // when: the group is deleted
        var response = client.delete("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: return is no content
        assertThat(response.status).isEqualTo(OK)

        // when: The deleted group is received
        response = client.get("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: it is not found
        assertThat(response.status).isEqualTo(NotFound)
    }

    @Test
    fun `Delete response has index links`() = spec.componentSpec {
        // when: the group is deleted
        val response = client.delete("/groups/$DEFAULT_GROUP_NAME") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: return is no content
        assertThat(response.status).isEqualTo(OK)

        // and: has index links
        val index: Index = json.decodeFromString(response.bodyAsText())

        assertEquals(3, index.links.size)
        assertLink("_self", "/", "GET", index.links)
        assertLink("get_groups", "/groups", "GET", index.links)
        assertLink("add_group", "/groups", "POST", index.links)
    }

    @Test
    fun `Delete unknown group - not found`() = spec.componentSpec {
        // when: an unknown group is received
        val response = client.get("/groups/UNKNOWN") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: it is not found
        assertThat(response.status).isEqualTo(NotFound)
    }
}
