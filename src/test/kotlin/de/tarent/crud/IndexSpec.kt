package de.tarent.crud

import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.contentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IndexSpec: BaseComponentSpec() {

    @Test
    fun `GET on index page returns link list`() = componentTest {
        val response = client.get("/") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        assertEquals(OK, response.status)
        assertEquals(expectedJson, response.bodyAsText())
    }

    private val expectedJson = """
       |{
       |  "links" : {
       |    "_self" : {
       |      "name" : "_self",
       |      "href" : "/",
       |      "method" : "GET"
       |    },
       |    "get_groups" : {
       |      "name" : "get_groups",
       |      "href" : "/groups",
       |      "method" : "GET"
       |    },
       |    "add_groups" : {
       |      "name" : "add_groups",
       |      "href" : "/groups",
       |      "method" : "POST"
       |    },
       |    "get_devices" : {
       |      "name" : "get_devices",
       |      "href" : "/devices",
       |      "method" : "GET"
       |    }
       |  }
       |}
    """.trimMargin("|")
}