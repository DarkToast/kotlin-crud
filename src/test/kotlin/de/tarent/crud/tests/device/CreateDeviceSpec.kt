package de.tarent.crud.tests.device

import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.contentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CreateDeviceSpec : BaseDeviceSpec() {
    private val spec = Spec().withSetup {
        createGroup(this, testGroupName, "my-test-group")
    }

    @Test
    fun `Create a device`() = spec.componentSpec {
        // given: A device request
        val request = deviceJson

        // when: We make a POST
        val response = client.post("/groups/$testGroupName/devices") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(request)
        }

        // then: Status Created with an URL
        assertEquals(Created, response.status)
        assertEquals("/groups/$testGroupName/devices/steckdose_lüftung", response.headers[HttpHeaders.Location])
    }

    @Test
    fun `devices with same name but in other groups are ok`() = spec.componentSpec {
        // given: A device with the same name in another group
        createGroup(this, "otherGroupName", "another group")
        createDevice(this, "otherGroupName", deviceJson)

        // and: A request
        val request = deviceJson

        // when: We make a POST
        val response = client.post("/groups/$testGroupName/devices") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(request)
        }

        // then: Status Created
        assertEquals(Created, response.status)
    }

    @Test
    fun `Failed creation - bad request`() = spec.componentSpec {
        // given: An invalid Json request
        val invalidRequest = "{}"

        // when: We make a post
        val response = client.post("/groups/$testGroupName/devices") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(invalidRequest)
        }

        // then: Status is Bad Request
        assertEquals(BadRequest, response.status)
    }

    @Test
    fun `Failed creation - existing`() = spec.componentSpec {
        // given: An existing device with the same name
        createDevice(this, testGroupName, deviceJson)

        // when: We make a post
        val response = client.post("/groups/$testGroupName/devices") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(deviceJson)
        }

        // then: Status Conflict
        assertEquals(Conflict, response.status)
    }

    @Test
    fun `Failed creation - wrong group`() = spec.componentSpec {
        // when: We make a post on an unknown group
        val response = client.post("/groups/unknown/devices") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(deviceJson)
        }

        // then: Status Not Found
        assertEquals(NotFound, response.status)
    }

    private val deviceJson = deviceJson("steckdose_lüftung", "Steckdose für die Lüftung", "plug")
}