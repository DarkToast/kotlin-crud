package de.tarent.crud.domain

import de.tarent.crud.domain.Method.DELETE
import de.tarent.crud.domain.Method.GET
import de.tarent.crud.domain.Method.PUT
import kotlinx.serialization.Serializable
import java.net.URI
import java.util.UUID
import java.util.UUID.randomUUID

@Serializable
data class Device(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = randomUUID(),
    val name: String,
    val description: String,
    val type: String,
) : Linked<Device>() {
    init {
        require(type.length <= 32) { throw DomainException("Type must not be greater than 32 characters.") }
        require(name.length <= 50) { throw DomainException("Name must not be greater than 50 characters.") }
        require(description.length <= 250) {
            throw DomainException("Description must not be greater than 250 characters.")
        }
    }

    fun withLinks(groupName: String): Device =
        this.addLink("_self", GET, URI("/groups/$groupName/devices/$name"))
            .addLink("update", PUT, URI("/groups/$groupName/devices/$name"))
            .addLink("delete", DELETE, URI("/groups/$groupName/devices/$name"))
            .addLink("get_devices", GET, URI("/groups/$groupName/devices"))
            .addLink("get_group", GET, URI("/groups/$groupName"))
}
