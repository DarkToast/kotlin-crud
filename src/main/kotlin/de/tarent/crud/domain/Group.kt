package de.tarent.crud.domain

import kotlinx.serialization.Serializable
import java.net.URI
import java.util.UUID
import java.util.UUID.randomUUID

@Serializable
data class Group(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = randomUUID(),
    val name: String,
    val description: String,
) : Linked<Group>() {
    init {
        require(name.length <= 50) { throw DomainException("Name must not be greater than 50 characters.") }
        require(description.length <= 250) {
            throw DomainException("Description must not be greater than 250 characters.")
        }
    }

    fun withLinks(): Group =
        this.addLink("_self", Method.GET, URI("/groups/$name"))
            .addLink("index", Method.GET, URI("/"))
            .addLink("delete", Method.DELETE, URI("/groups/$name"))
            .addLink("update", Method.PUT, URI("/groups/$name"))
            .addLink("add_device", Method.POST, URI("/groups/$name/devices"))
            .addLink("list_devices", Method.GET, URI("/groups/$name/devices"))
}
