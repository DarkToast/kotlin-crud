package de.tarent.crud.tests.device

import de.tarent.crud.dtos.Device
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.serialization.builtins.ListSerializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Suppress("unused")
class ReadDeviceSpec : BaseDeviceSpec() {
    private val spec = Spec().withSetup {
        createGroup(this, testGroupName, "my-test-group")
        createDevice(this, testGroupName, deviceJson(testDeviceName, "test-device", "plug"))
    }

    @Test
    @Disabled("still WiP")
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
        val device = list[0]
        assertEquals(testDeviceName, device.name)
        assertEquals("test-device", device.description)
        assertEquals("plug", device.type)
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