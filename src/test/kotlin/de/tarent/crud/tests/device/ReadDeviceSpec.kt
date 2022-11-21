package de.tarent.crud.tests.device

import de.tarent.crud.dtos.Device
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReadDeviceSpec : BaseDeviceSpec() {
    private val spec = Spec().withSetup {
        createGroup(this, testGroupName, "my-test-group")
        createDevice(this, testGroupName, deviceJson(testDeviceName, "test-device", "plug"))
        createDevice(this, testGroupName, deviceJson("$testDeviceName-2", "test-device-2", "plug"))
    }

    @Test
    fun `get all devices for group`() = spec.componentSpec {
        // given: the device url of the test group
        val url = "/groups/$testGroupName/devices"

        // when: a GET is made
        val response = client.get(url) {
            accept(ContentType.Application.Json)
        }

        // then: status ok is returned
        assertEquals(OK, response.status)

        // and: The list contains two devices
        val body: String = response.bodyAsText()
        val list: List<Device> = json.decodeFromString(ListSerializer(Device.serializer()), body)
        assertEquals(2, list.size)

        // and: The test devices was returned
        assertDevice(testDeviceName, "test-device", "plug", list[0])
        assertDevice("$testDeviceName-2", "test-device-2", "plug", list[1])

        // and: Each device has related links
        assertEquals(5, list[0].links.size)
        assertEquals(5, list[1].links.size)
    }

    @Test
    fun `get all devices for group and not another`() = Spec().componentSpec {
        // given: Two groups
        createGroup(this, "first_group", "my-1-test-group")
        createGroup(this, "second_group", "my-2-test-group")

        // and: Two devices for group 1
        createDevice(this, "first_group", deviceJson("first_device", "test-device", "plug"))
        createDevice(this, "first_group", deviceJson("second_device", "test-device", "plug"))

        // and: A device for group 2
        createDevice(this, "second_group", deviceJson("third_device", "test-device", "plug"))

        // given: the device url of group 1
        val url = "/groups/first_group/devices"

        // when: a GET is made
        val response = client.get(url) {
            accept(ContentType.Application.Json)
        }

        // then: status ok is returned
        assertEquals(OK, response.status)

        // and: The list contains two devices
        val body: String = response.bodyAsText()
        val list: List<Device> = json.decodeFromString(ListSerializer(Device.serializer()), body)

        assertEquals(2, list.size)

        // and: The test devices was returned
        assertDevice("first_device", "test-device", "plug", list[0])
        assertDevice("second_device", "test-device", "plug", list[1])
    }

    @Test
    fun `all devices of an unknown group fails`() = Spec().componentSpec {
        // given: url with unknown group
        val url = "/groups/unknown/devices"

        // when: a GET is made
        val response = client.get(url) {
            accept(ContentType.Application.Json)
        }

        // then: status not found is returned
        assertEquals(NotFound, response.status)
    }

    @Test
    fun `get device of a group`() = spec.componentSpec {
        // given: url of a single device
        val url = "/groups/$testGroupName/devices/$testDeviceName"

        // when: a GET is made
        val response = client.get(url) {
            accept(ContentType.Application.Json)
        }

        // then: status ok
        assertEquals(OK, response.status)

        // and: the device is returned
        val device: Device = json.decodeFromString(response.bodyAsText())
        assertDevice(testDeviceName, "test-device", "plug", device)

        // and: It has all related links
        assertLink("_self", "/groups/$testGroupName/devices/$testDeviceName", "GET", device.links)
        assertLink("update", "/groups/$testGroupName/devices/$testDeviceName", "PUT", device.links)
        assertLink("delete", "/groups/$testGroupName/devices/$testDeviceName", "DELETE", device.links)
        assertLink("get_devices", "/groups/$testGroupName/devices", "GET", device.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", device.links)
    }

    @Test
    fun `get device of an unknown group fails`() = spec.componentSpec {
        // given: url with unknown group
        val url = "/groups/unknown/devices/$testDeviceName"

        // when: a GET is made
        val response = client.get(url) {
            accept(ContentType.Application.Json)
        }

        // then: status not found is returned
        assertEquals(NotFound, response.status)
    }

    @Test
    fun `get unknown device fails`() = spec.componentSpec {
        // given: url with unknown group
        val url = "/groups/$testGroupName/devices/unknown"

        // when: a GET is made
        val response = client.get(url) {
            accept(ContentType.Application.Json)
        }

        // then: status not found is returned
        assertEquals(NotFound, response.status)
    }


}