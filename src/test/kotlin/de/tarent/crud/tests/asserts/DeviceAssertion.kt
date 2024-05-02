package de.tarent.crud.tests.asserts

import de.tarent.crud.driver.rest.dtos.DeviceResponse
import io.ktor.client.statement.HttpResponse
import org.junit.jupiter.api.Assertions

interface DeviceAssertion : LinkAssertion {
    suspend fun assertDevice(
        name: String,
        description: String,
        type: String,
        response: HttpResponse,
    ): Boolean {
        return Assertion.assert<DeviceResponse>(response) { assertDevice(name, description, type, it) }
    }

    fun assertDevice(
        name: String,
        description: String,
        type: String,
        device: DeviceResponse,
    ): Boolean {
        Assertions.assertEquals(name, device.name)
        Assertions.assertEquals(description, device.description)
        Assertions.assertEquals(type, device.type)
        return true
    }
}
