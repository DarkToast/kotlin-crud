package de.tarent.crud.tests.device

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.http.HttpStatusCode.Companion.NotFound
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeleteDeviceSpec : BaseDeviceSpec() {

    private val spec = Spec().withSetup {
        createGroup(this, testGroupName, "my-test-group")
        createDevice(this, testGroupName, deviceJson(testDeviceName, "test-device", "plug"))
    }

    @Test
    fun `deleting existing device`() = spec.componentSpec {
        // given: The device url
        val url = "/groups/$testGroupName/devices/$testDeviceName"

        // when: We make DELETE
        var response = client.delete(url)

        // then: Status No Content
        assertEquals(NoContent, response.status)

        // when: We make GET
        response = client.get(url)

        // then: Status Not Found
        assertEquals(NotFound, response.status)
    }

    @Test
    fun `deleting unknown device`() = spec.componentSpec {
        // given: Non existing device url
        val url = "/groups/$testGroupName/devices/unknown"

        // when: We make DELETE
        val response = client.delete(url)

        // then: Status Not Found
        assertEquals(NotFound, response.status)
    }

    @Test
    fun `delete on unknown group`() = spec.componentSpec {
        // given: url with unknown group
        val url = "/groups/unknown/devices/$testDeviceName"

        // when: We make DELETE
        val response = client.delete(url)

        // then: Status Not Found
        assertEquals(NotFound, response.status)
    }
}