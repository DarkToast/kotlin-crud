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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CreateDeviceSpec : BaseDeviceSpec() {
    val groupName = "testGroup"


    val spec = Spec().withSetup {
        createGroup(this, groupName, "my-test-group")
    }

    @Test
    fun `Create a device`() = spec.componentSpec {
        val response = client.post("/groups/$groupName/devices") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(deviceJson)
        }

        Assertions.assertEquals(Created, response.status)
        Assertions.assertEquals("/groups/$groupName/devices/steckdose_lüftung", response.headers[HttpHeaders.Location])
    }

    @Test
    fun `devices with same name but in other groups are ok`() = spec.componentSpec {
        createGroup(this, "otherGroupName", "another group")
        createDevice(this, "otherGroupName", deviceJson)

        val response = client.post("/groups/$groupName/devices") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(deviceJson)
        }

        Assertions.assertEquals(Created, response.status)
    }

    @Test
    fun `Failed creation - bad request`() = spec.componentSpec {
        val response = client.post("/groups/$groupName/devices") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody("{}")
        }

        Assertions.assertEquals(BadRequest, response.status)
    }

    @Test
    fun `Failed creation - existing`() = spec.componentSpec {
        createDevice(this, groupName, deviceJson)

        val response = client.post("/groups/testGroup/devices") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(deviceJson)
        }

        Assertions.assertEquals(Conflict, response.status)
    }

    @Test
    fun `Failed creation - wrong group`() = spec.componentSpec {
        createDevice(this, groupName, deviceJson)

        val response = client.post("/groups/otherGroup/devices") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(deviceJson)
        }

        Assertions.assertEquals(NotFound, response.status)
    }

    private val deviceJson = deviceJson("steckdose_lüftung", "Steckdose für die Lüftung", "plug")

}