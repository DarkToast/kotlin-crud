package de.tarent.crud.domain

import java.util.UUID
import java.util.UUID.randomUUID

data class Group(
    val id: UUID = randomUUID(),
    val name: String,
    val description: String,
) {
    init {
        require(name.length <= 50) { throw DomainException("Name must not be greater than 50 characters.") }
        require(description.length <= 250) {
            throw DomainException("Description must not be greater than 250 characters.")
        }
    }
}
