package de.tarent.crud.tests.device

import de.tarent.crud.dtos.Device
import de.tarent.crud.tests.BaseComponentSpec
import org.junit.jupiter.api.Assertions.assertEquals

@Suppress("SameParameterValue")
abstract class BaseDeviceSpec : BaseComponentSpec() {
    val testGroupName = "testGroup"
    val testDeviceName = "deviceName"

    protected fun assertDevice(name: String, description: String, type: String, device: Device): Boolean {
        assertEquals(name, device.name)
        assertEquals(description, device.description)
        assertEquals(type, device.type)
        return true
    }
}