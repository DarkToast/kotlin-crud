package de.tarent.crud.tests.device

import de.tarent.crud.dtos.Device
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UpdateDeviceSpec : BaseDeviceSpec() {
    private val spec = Spec().withSetup {
        createGroup(this, testGroupName, "my-test-group")
        createDevice(this, testGroupName, deviceJson(testDeviceName, "test-device", "plug"))

        createDevice(this, testGroupName, deviceJson("conflicting_device", "test-device-2", "plug"))
    }

    @Test
    fun `update existing device`() = spec.componentSpec {
        // given: the device url
        val url = "/groups/$testGroupName/devices/$testDeviceName"

        // and: A new client json
        val request = deviceJson(testDeviceName, "my-new-description", "switch")

        // when: We make a PUT
        val response = client.put(url) {
            setBody(request)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: Status OK is returned
        assertEquals(OK, response.status)

        // and: The new device as body
        val device: Device = json.decodeFromString(response.bodyAsText())
        assertDevice(testDeviceName, "my-new-description", "switch", response)

        // and: It has all related links
        assertLink("_self", "/groups/$testGroupName/devices/$testDeviceName", "GET", device.links)
        assertLink("update", "/groups/$testGroupName/devices/$testDeviceName", "PUT", device.links)
        assertLink("delete", "/groups/$testGroupName/devices/$testDeviceName", "DELETE", device.links)
        assertLink("get_devices", "/groups/$testGroupName/devices", "GET", device.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", device.links)
    }

    @Test
    fun `Renaming existing device`() = spec.componentSpec {
        // given: the device url
        var url = "/groups/$testGroupName/devices/$testDeviceName"

        // and: A new client json
        val request = deviceJson("my-new-name", "my-new-description", "switch")

        // when: We make a PUT
        var response = client.put(url) {
            setBody(request)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: Status OK is returned
        assertEquals(OK, response.status)

        // and: The renamed device as body
        val device: Device = json.decodeFromString(response.bodyAsText())
        assertDevice("my-new-name", "my-new-description", "switch", response)

        // and: It has all related links
        assertLink("_self", "/groups/$testGroupName/devices/my-new-name", "GET", device.links)
        assertLink("update", "/groups/$testGroupName/devices/my-new-name", "PUT", device.links)
        assertLink("delete", "/groups/$testGroupName/devices/my-new-name", "DELETE", device.links)
        assertLink("get_devices", "/groups/$testGroupName/devices", "GET", device.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", device.links)

        // when: We make a Get on the new url
        url = "/groups/$testGroupName/devices/my-new-name"
        response = client.get(url)

        // then: Status OK is returned
        assertEquals(OK, response.status)
        assertDevice("my-new-name", "my-new-description", "switch", response)
    }

    @Test
    fun `Renaming conflicts`() = spec.componentSpec {
        // given: the device url
        val url = "/groups/$testGroupName/devices/$testDeviceName"

        // and: A new client json
        val request = deviceJson("conflicting_device", "my-new-description", "switch")

        // when: We make a PUT
        val response = client.put(url) {
            setBody(request)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: Status Conflict is returned
        assertEquals(Conflict, response.status)
    }

    @Test
    fun `device does not exists`() = spec.componentSpec {
        // given: the unknown device url
        val url = "/groups/$testGroupName/devices/unknown"

        // and: A new client json
        val request = deviceJson("conflicting_device", "my-new-description", "switch")

        // when: We make a PUT
        val response = client.put(url) {
            setBody(request)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: Status Conflict is returned
        assertEquals(NotFound, response.status)
    }

    @Test
    fun `group does not exists`() = spec.componentSpec {
        // given: the unknown device url
        val url = "/groups/unknown/devices/$testDeviceName"

        // and: A new client json
        val request = deviceJson("conflicting_device", "my-new-description", "switch")

        // when: We make a PUT
        val response = client.put(url) {
            setBody(request)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        // then: Status Conflict is returned
        assertEquals(NotFound, response.status)
    }
}