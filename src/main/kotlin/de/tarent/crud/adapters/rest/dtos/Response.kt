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
import kotlinx.serialization.Transient

@Serializable
class Response<P>(val payload: P) : Linked<Response<P>>() {
    init {
        when (this.payload) {
            is Group -> groupLinks(payload)
            is Device -> deviceLinks(payload)
            is Metric -> metricLink(payload)
        }
    }

    private fun groupLinks(group: Group) {
        val name = group.name
        this.addLink("index", GET, "/")
            .addLink("_self", GET, "/groups/$name")
            .addLink("delete", DELETE, "/groups/$name")
            .addLink("update", PUT, "/groups/$name")
            .addLink("add_device", POST, "/groups/$name/devices")
            .addLink("list_devices", GET, "/groups/$name/devices")
            .addLink("list_groups", GET, "/groups")
    }

    private fun deviceLinks(device: Device) {
        val name = device.name
        val groupName = device.groupName

        this.addLink("index", GET, "/")
            .addLink("_self", GET, "/groups/$groupName/devices/$name")
            .addLink("update", PUT, "/groups/$groupName/devices/$name")
            .addLink("delete", DELETE, "/groups/$groupName/devices/$name")
            .addLink("add_metric", POST, "/groups/$groupName/devices/$name/metrics")
            .addLink("query_metrics", GET, "/groups/$groupName/devices/$name/metrics?from={from}&to={to}")
            .addLink("list_devices", GET, "/groups/$groupName/devices")
            .addLink("get_group", GET, "/groups/$groupName")
    }

    private fun metricLink(metric: Metric) {
        val groupName = metric.groupName
        val deviceName = metric.deviceName
        val id = metric.id

        this.addLink("index", GET, "/")
            .addLink("_self", GET, "/groups/$groupName/devices/$deviceName/metrics/$id")
            .addLink("delete", DELETE, "/groups/$groupName/devices/$deviceName/metrics/$id")
            .addLink("query_metrics", GET, "/groups/$groupName/devices/$deviceName/metrics?from={from}&to={to}")
            .addLink("get_device", GET, "/groups/$groupName/devices/$deviceName")
            .addLink("get_group", GET, "/groups/$groupName")
    }
}

@Serializable
class GroupListResponse(val payload: List<Group>) : Linked<GroupListResponse>() {
    init {
        this.addLink("index", GET, "/")
            .addLink("_self", GET, "/groups")
            .addLink("get_group", GET, "/groups/{name}")
    }
}

@Serializable
class DeviceListResponse(
    @Transient val groupName: String = "",
    val payload: List<Device>,
) : Linked<DeviceListResponse>() {
    init {
        this.addLink("index", GET, "/")
            .addLink("_self", GET, "/groups/$groupName/devices")
            .addLink("get_group", GET, "/groups/$groupName")
            .addLink("get_device", GET, "/groups/$groupName/devices/{name}")
    }
}

@Serializable
class MetricListResponse(val payload: MetricList) : Linked<MetricListResponse>() {
    init {
        val type = if (payload.type != null) "&type=${payload.type}" else ""
        val query = "?from=${payload.from.toLocalDateTime()}&to=${payload.to.toLocalDateTime()}$type"
        val groupName = payload.groupName
        val deviceName = payload.deviceName

        this.addLink("index", GET, "/")
            .addLink("_self", GET, "/groups/$groupName/devices/$deviceName/metrics$query")
            .addLink("get_device", GET, "/groups/$groupName/devices/$deviceName")
            .addLink("get_group", GET, "/groups/$groupName")
            .addLink("get_metric", GET, "/groups/$groupName/devices/$deviceName/{id}")
    }
}
