package de.tarent.crud.tests.device

import de.tarent.crud.dtos.Failure
import de.tarent.crud.dtos.Group
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.serialization.decodeFromString
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
    fun `deleting device has related links`() = spec.componentSpec {
        // given: The device url
        val url = "/groups/$testGroupName/devices/$testDeviceName"

        // when: We make DELETE
        val response = client.delete(url)

        // then: Status Ok
        assertEquals(OK, response.status)

        // and: The group is returned
        val group: Group = json.decodeFromString(response.bodyAsText())
        assertGroup(testGroupName, "my-test-group", group)

        assertLink("index", "/", "GET", group.links)
        assertLink("_self", "/groups/$testGroupName", "GET", group.links)
        assertLink("delete", "/groups/$testGroupName", "DELETE", group.links)
        assertLink("update", "/groups/$testGroupName", "PUT", group.links)
        assertLink("add_device", "/groups/$testGroupName/devices", "POST", group.links)
        assertLink("list_devices", "/groups/$testGroupName/devices", "GET", group.links)
    }

    @Test
    fun `deleting unknown device`() = spec.componentSpec {
        // given: Non existing device url
        val url = "/groups/$testGroupName/devices/unknown"

        // when: We make DELETE
        val response = client.delete(url)

        // then: Status Not Found
        assertEquals(NotFound, response.status)

        // and: It has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", failure.links)
        assertLink("get_devices", "/groups/$testGroupName/devices", "GET", failure.links)
    }

    @Test
    fun `delete on unknown group`() = spec.componentSpec {
        // given: url with unknown group
        val url = "/groups/unknown/devices/$testDeviceName"

        // when: We make DELETE
        val response = client.delete(url)

        // then: Status Not Found
        assertEquals(NotFound, response.status)

        // and: The failure has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_groups", "/groups", "GET", failure.links)
        assertLink("add_group", "/groups", "POST", failure.links)
    }
}