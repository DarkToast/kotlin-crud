package de.tarent.crud.tests.device

import de.tarent.crud.tests.BaseComponentSpec
import de.tarent.crud.tests.asserts.DeviceAssertion
import de.tarent.crud.tests.asserts.GroupAssertion

abstract class BaseDeviceSpec : BaseComponentSpec(), DeviceAssertion, GroupAssertion {
    val testGroupName = "testGroup"
    val testDeviceName = "deviceName"
    val testDeviceName2 = "deviceName-2"

    protected val spec = Spec().withSetup {
        createGroup(this, testGroupName, "my-test-group")
        createDevice(this, testGroupName, deviceJson(testDeviceName, "test-device", "plug"))
        createDevice(this, testGroupName, deviceJson(testDeviceName2, "test-device-2", "plug"))
    }
}