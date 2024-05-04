package de.tarent.crud.domain

import kotlinx.serialization.Serializable
import java.util.UUID
import java.util.UUID.randomUUID

@Serializable
data class Device(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = randomUUID(),
    val name: String,
    val description: String,
    val type: String,
    val groupName: String,
) {
    init {
        require(type.length <= 32) { throw DomainException("Type must not be greater than 32 characters.") }
        require(name.length <= 50) { throw DomainException("Name must not be greater than 50 characters.") }
        require(description.length <= 250) {
            throw DomainException("Description must not be greater than 250 characters.")
        }
    }
}
