package de.tarent.crud.adapters.graphql

import com.expediagroup.graphql.server.operations.Query
import de.tarent.crud.application.DeviceService
import de.tarent.crud.application.GroupService
import de.tarent.crud.application.results.GroupDontExists
import de.tarent.crud.application.results.Ok
import de.tarent.crud.domain.Device
import de.tarent.crud.domain.Group

class DeviceQuery(private val deviceService: DeviceService) : Query {
    fun devicesByGroup(groupName: String): List<Device> =
        when (val result = deviceService.listDevices(groupName)) {
            is GroupDontExists -> emptyList()
            is Ok -> result.value
        }
}

class GroupQuery(private val groupServic: GroupService) : Query {
    fun groups(): List<Group> =
        when (val result = groupServic.list()) {
            is Ok -> result.value
        }

    fun get(groupName: String): Group? =
        when (val result = groupServic.read(groupName)) {
            is Ok -> result.value
            is GroupDontExists -> null
        }
}
