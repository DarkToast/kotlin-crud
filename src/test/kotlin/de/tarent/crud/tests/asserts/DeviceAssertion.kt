package de.tarent.crud.tests.asserts

import de.tarent.crud.domain.Device
import io.ktor.client.statement.HttpResponse
import org.junit.jupiter.api.Assertions

interface DeviceAssertion : LinkAssertion {
    suspend fun assertDevice(
        name: String,
        description: String,
        type: String,
        response: HttpResponse,
    ): Boolean {
        return Assertion.assert<Device>(response) { assertDevice(name, description, type, it) }
    }

    fun assertDevice(
        name: String,
        description: String,
        type: String,
        device: Device,
    ): Boolean {
        Assertions.assertEquals(name, device.name)
        Assertions.assertEquals(description, device.description)
        Assertions.assertEquals(type, device.type)
        return true
    }
}
