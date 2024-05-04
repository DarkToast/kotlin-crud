package de.tarent.crud.adapters.graphql.schema

import de.tarent.crud.domain.DomainException
import de.tarent.crud.domain.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID.randomUUID

@Serializable
data class Device(
    @Serializable(with = UUIDSerializer::class)
    val id: String = randomUUID().toString(),
    val name: String,
    val description: String,
    val type: String,
) {
    init {
        require(type.length <= 32) { throw DomainException("Type must not be greater than 32 characters.") }
        require(name.length <= 50) { throw DomainException("Name must not be greater than 50 characters.") }
        require(description.length <= 250) {
            throw DomainException("Description must not be greater than 250 characters.")
        }
    }
}
