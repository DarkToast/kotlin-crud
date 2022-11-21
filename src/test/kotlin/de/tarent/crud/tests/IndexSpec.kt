package de.tarent.crud.tests

import de.tarent.crud.dtos.Index
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.contentType
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IndexSpec : BaseComponentSpec() {

    @Test
    fun `GET on index page returns link list`() = Spec().componentSpec {
        val response = client.get("/") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        assertEquals(OK, response.status)
        val index: Index = json.decodeFromString(response.bodyAsText())

        assertEquals(3, index.links.size)
        assertLink("_self", "/", "GET", index.links)
        assertLink("get_groups", "/groups", "GET", index.links)
        assertLink("add_group", "/groups", "POST", index.links)
    }
}