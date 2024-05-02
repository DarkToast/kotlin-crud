package de.tarent.crud.driver.rest.dtos

import de.tarent.crud.domain.Device
import de.tarent.crud.domain.Linked
import de.tarent.crud.domain.Method
import de.tarent.crud.domain.UUIDSerializer
import kotlinx.serialization.Serializable
import java.net.URI
import java.util.UUID

@Serializable
data class DeviceResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val type: String,
) : Linked<DeviceResponse>() {
    companion object {
        fun from(device: Device) =
            DeviceResponse(
                id = device.id,
                name = device.name.toString(),
                description = device.description.toString(),
                type = device.type.toString(),
            )
    }

    fun withLinks(groupName: String): DeviceResponse =
        this.addLink(
            "_self",
            Method.GET,
            URI("/groups/$groupName/devices/$name"),
        )
            .addLink("update", Method.PUT, URI("/groups/$groupName/devices/$name"))
            .addLink("delete", Method.DELETE, URI("/groups/$groupName/devices/$name"))
            .addLink("get_devices", Method.GET, URI("/groups/$groupName/devices"))
            .addLink("get_group", Method.GET, URI("/groups/$groupName"))
}

@Serializable
data class CreateUpdateDeviceRequest(
    val name: String,
    val description: String,
    val type: String,
)
