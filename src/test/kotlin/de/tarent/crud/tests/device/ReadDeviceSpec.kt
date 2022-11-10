package de.tarent.crud.tests.device

import de.tarent.crud.dtos.Device
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.serialization.builtins.ListSerializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@Suppress("unused")
class ReadDeviceSpec : BaseDeviceSpec() {
    private val spec = Spec().withSetup {
        createGroup(this, testGroupName, "my-test-group")
        createDevice(this, testGroupName, deviceJson(testDeviceName, "test-device", "plug"))
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

        // and: The list contains one device
        val body: String = response.bodyAsText()
        val list: List<Device> = json.decodeFromString(ListSerializer(Device.serializer()), body)

        assertEquals(1, list.size)

        // and: The test device was returned
        assertDevice(testDeviceName, "test-device", "plug", list[0])
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

    fun `all devices of an unknown group fails`() {

    }

    fun `get device of a group`() {

    }

    fun `get device of an unknown group fails`() {

    }

    fun `get unknown device fails`() {

    }


}