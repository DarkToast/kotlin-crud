package de.tarent.crud.tests

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode.Companion.OK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AdminSpec : BaseComponentSpec() {
    @Test
    fun `GET status resource`() = Spec().componentSpec {
        val response = client.get("/admin/status")
        assertThat(response.status).isEqualTo(OK)
    }

    @Test
    fun `GET on health resource`() = Spec().componentSpec {
        val response = client.get("/admin/health")
        assertThat(response.status).isEqualTo(OK)
    }
}