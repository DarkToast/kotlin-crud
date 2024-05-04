package de.tarent.crud.adapters.rest.dtos

import de.tarent.crud.adapters.rest.dtos.Method.DELETE
import de.tarent.crud.adapters.rest.dtos.Method.GET
import de.tarent.crud.adapters.rest.dtos.Method.POST
import de.tarent.crud.adapters.rest.dtos.Method.PUT
import de.tarent.crud.domain.Device
import de.tarent.crud.domain.Group
import de.tarent.crud.domain.Metric
import de.tarent.crud.domain.MetricList
import kotlinx.serialization.Serializable

@Serializable
abstract class Response<P> : Linked<Response<P>>() {
    abstract val payload: P
}

@Serializable
class GroupResponse(override val payload: Group) : Response<Group>() {
    init {
        val name = payload.name
        this.addLink("_self", GET, "/groups/$name")
            .addLink("index", GET, "/")
            .addLink("delete", DELETE, "/groups/$name")
            .addLink("update", PUT, "/groups/$name")
            .addLink("add_device", POST, "/groups/$name/devices")
            .addLink("list_devices", GET, "/groups/$name/devices")
    }
}

@Serializable
class GroupListResponse(override val payload: List<Group>) : Response<List<Group>>() {
    init {
        this.addLink("index", GET, "/")
            .addLink("_self", GET, "/groups")
            .addLink("get_group", GET, "/groups/{name}")
    }
}

@Serializable
class DeviceResponse(override val payload: Device) : Response<Device>() {
    init {
        val name = payload.name
        val groupName = payload.groupName

        this.addLink("_self", GET, "/groups/$groupName/devices/$name")
            .addLink("update", PUT, "/groups/$groupName/devices/$name")
            .addLink("delete", DELETE, "/groups/$groupName/devices/$name")
            .addLink("get_devices", GET, "/groups/$groupName/devices")
            .addLink("get_group", GET, "/groups/$groupName")
    }
}

@Serializable
class DeviceListResponse(val groupName: String, override val payload: List<Device>) : Response<List<Device>>() {
    init {
        this.addLink("index", GET, "/")
            .addLink("_self", GET, "/groups")
            .addLink("get_group", GET, "/groups/$groupName")
    }
}

@Serializable
class MetricResponse(override val payload: Metric) : Response<Metric>() {
    init {
        val groupName = payload.groupName
        val deviceName = payload.deviceName
        val id = payload.id

        this.addLink("_self", GET, "/groups/$groupName/devices/$deviceName/metrics/$id")
            .addLink("delete", DELETE, "/groups/$groupName/devices/$deviceName/metrics/$id")
            .addLink("get_device", GET, "/groups/$groupName/devices/$deviceName")
            .addLink("get_group", GET, "/groups/$groupName")
    }
}

@Serializable
class MetricListResponse(override val payload: MetricList) : Response<MetricList>() {
    init {
        val type = if (payload.type != null) "&type=${payload.type}" else ""
        val query = "?from=${payload.from.toLocalDateTime()}&to=${payload.to.toLocalDateTime()}$type"
        val groupName = payload.groupName
        val deviceName = payload.deviceName

        this.addLink("_self", GET, "/groups/$groupName/devices/$deviceName/metrics$query")
            .addLink("get_device", GET, "/groups/$groupName/devices/$deviceName")
            .addLink("get_group", GET, "/groups/$groupName")
    }
}
