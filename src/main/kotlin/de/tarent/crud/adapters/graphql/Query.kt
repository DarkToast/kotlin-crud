package de.tarent.crud.adapters.graphql

import com.expediagroup.graphql.server.operations.Query
import de.tarent.crud.application.DeviceService
import de.tarent.crud.application.GroupService
import de.tarent.crud.application.MetricService
import de.tarent.crud.application.results.GroupDontExists
import de.tarent.crud.application.results.Ok
import de.tarent.crud.domain.Device
import de.tarent.crud.domain.Group

class MetricQuery(private val metricService: MetricService) : Query {
    fun metrics(
        groupName: String,
        deviceName: String,
        from: String?,
        to: String?,
        type: String?,
    ): List<Device> {
        TODO()
    }
}

class DeviceQuery(private val deviceService: DeviceService) : Query {
    fun devicesByGroup(groupName: String): List<Device> =
        when (val result = deviceService.listDevices(groupName)) {
            is GroupDontExists -> emptyList()
            is Ok -> result.value
        }
}

class GroupQuery(private val groupService: GroupService) : Query {
    fun groups(): List<Group> =
        when (val result = groupService.list()) {
            is Ok -> result.value
        }

    fun get(groupName: String): Group? =
        when (val result = groupService.read(groupName)) {
            is Ok -> result.value
            is GroupDontExists -> null
        }
}
