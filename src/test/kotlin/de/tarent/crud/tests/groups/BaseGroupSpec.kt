package de.tarent.crud.tests.groups

import de.tarent.crud.tests.BaseComponentSpec
import de.tarent.crud.tests.asserts.GroupAssertion

abstract class BaseGroupSpec : BaseComponentSpec(), GroupAssertion {
    open val spec =
        Spec().withSetup {
            createGroup(this, DEFAULT_GROUP_NAME, "Hauswirtschaftsraum")
            createDevice(this, DEFAULT_GROUP_NAME, deviceJson(DEFAULT_GROUP_DEVICE_NAME1, "", "plug"))
            createDevice(this, DEFAULT_GROUP_NAME, deviceJson(DEFAULT_GROUP_DEVICE_NAME2, "", "plug"))
        }

    companion object {
        const val DEFAULT_GROUP_NAME: String = "HWR"
        const val DEFAULT_GROUP_DEVICE_NAME1 = "groupSpec_device1"
        const val DEFAULT_GROUP_DEVICE_NAME2 = "groupSpec_device2"
        val DEFAULT_DEVICE_LIST = listOf(DEFAULT_GROUP_DEVICE_NAME1, DEFAULT_GROUP_DEVICE_NAME2)
    }
}
