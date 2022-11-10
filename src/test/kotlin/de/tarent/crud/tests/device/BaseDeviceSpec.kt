package de.tarent.crud.tests.device

import de.tarent.crud.dtos.Device
import de.tarent.crud.tests.BaseComponentSpec
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Assertions.assertEquals

abstract class BaseDeviceSpec : BaseComponentSpec() {
    val testGroupName = "testGroup"
    val testDeviceName = "deviceName"

    protected suspend fun assertDevice(
        name: String,
        description: String,
        type: String,
        response: HttpResponse
    ): Boolean {
        val device: Device = json.decodeFromString(response.bodyAsText())
        return assertDevice(name, description, type, device)
    }

    protected fun assertDevice(name: String, description: String, type: String, device: Device): Boolean {
        assertEquals(name, device.name)
        assertEquals(description, device.description)
        assertEquals(type, device.type)
        return true
    }
}