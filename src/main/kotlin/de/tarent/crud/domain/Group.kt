package de.tarent.crud.domain

import java.util.UUID

data class Name(val value: String) {
    init {
        require(value.length <= 50) { throw DomainException("Name must not be greater than 50 characters.") }
    }

    override fun toString(): String = value
}

data class Description(val value: String) {
    init {
        require(value.length <= 250) { throw DomainException("Description must not be greater than 50 characters.") }
    }

    override fun toString(): String = value
}

data class Group(
    val id: UUID = UUID.randomUUID(),
    val name: Name,
    val description: Description
)
