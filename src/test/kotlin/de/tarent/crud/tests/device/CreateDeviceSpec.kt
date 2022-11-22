package de.tarent.crud.tests.device

import de.tarent.crud.dtos.Device
import de.tarent.crud.dtos.Failure
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.contentType
import kotlinx.serialization.decodeFromString
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

        //and: the device is returned
        val device: Device = json.decodeFromString(response.bodyAsText())
        assertDevice("steckdose_lüftung", "Steckdose für die Lüftung", "plug", device)

        // and: It has all related links
        assertLink("_self", "/groups/$testGroupName/devices/steckdose_lüftung", "GET", device.links)
        assertLink("update", "/groups/$testGroupName/devices/steckdose_lüftung", "PUT", device.links)
        assertLink("delete", "/groups/$testGroupName/devices/steckdose_lüftung", "DELETE", device.links)
        assertLink("get_devices", "/groups/$testGroupName/devices", "GET", device.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", device.links)
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

        // and: It has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", failure.links)
        assertLink("get_devices", "/groups/$testGroupName/devices", "GET", failure.links)
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

        // and: It has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", failure.links)
        assertLink("get_devices", "/groups/$testGroupName/devices", "GET", failure.links)
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

        // and: The failure has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_groups", "/groups", "GET", failure.links)
        assertLink("add_group", "/groups", "POST", failure.links)
    }

    private val deviceJson = deviceJson("steckdose_lüftung", "Steckdose für die Lüftung", "plug")
}