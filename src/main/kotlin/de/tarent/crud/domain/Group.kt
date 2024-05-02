package de.tarent.crud.domain

import java.util.UUID

data class Group(
    val id: UUID = UUID.randomUUID(),
    val name: Name,
    val description: Description,
)
