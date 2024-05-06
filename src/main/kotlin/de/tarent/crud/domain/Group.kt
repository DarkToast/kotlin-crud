package de.tarent.crud.domain

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val name: String,
    val description: String,
    val devices: List<String> = emptyList(),
) {
    init {
        require(name.length <= 50) { throw DomainException("Name must not be greater than 50 characters.") }
        require(description.length <= 250) {
            throw DomainException("Description must not be greater than 250 characters.")
        }
    }
}
