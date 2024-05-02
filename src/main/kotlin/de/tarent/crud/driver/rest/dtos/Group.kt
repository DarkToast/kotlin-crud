package de.tarent.crud.driver.rest.dtos

import de.tarent.crud.domain.Group
import de.tarent.crud.domain.Linked
import de.tarent.crud.domain.Method
import de.tarent.crud.domain.UUIDSerializer
import kotlinx.serialization.Serializable
import java.net.URI
import java.util.UUID

@Serializable
data class GroupResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val description: String,
) : Linked<GroupResponse>() {
    companion object {
        fun from(group: Group) =
            GroupResponse(
                id = group.id,
                name = group.name.toString(),
                description = group.description.toString(),
            )
    }

    fun withLinks(): GroupResponse =
        this.addLink("_self", Method.GET, URI("/groups/$name"))
            .addLink("index", Method.GET, URI("/"))
            .addLink("delete", Method.DELETE, URI("/groups/$name"))
            .addLink("update", Method.PUT, URI("/groups/$name"))
            .addLink("add_device", Method.POST, URI("/groups/$name/devices"))
            .addLink("list_devices", Method.GET, URI("/groups/$name/devices"))
}

@Serializable
data class CreateUpdateGroupRequest(
    val name: String,
    val description: String,
)
