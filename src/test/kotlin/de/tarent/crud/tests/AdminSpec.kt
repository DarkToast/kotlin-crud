package de.tarent.crud.tests

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AdminSpec : BaseComponentSpec() {
    @Test
    fun `GET status resource`() = Spec().componentSpec {
        val response = client.get("/admin/status")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET on health resource`() = Spec().componentSpec {
        val response = client.get("/admin/health")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}