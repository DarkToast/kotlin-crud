package de.tarent.crud.adapters.graphql

import com.expediagroup.graphql.server.operations.Query
import de.tarent.crud.adapters.graphql.schema.Device
import de.tarent.crud.application.DeviceService

class DeviceQuery(private val deviceService: DeviceService) : Query {
    fun devicesByGroup(groupName: String): List<Device> {
        return listOf(Device(name = "foobar", description = "fooDescr", type = "hp"))
    }
}
