package de.tarent.crud.domain

import java.util.UUID

data class Device(
    val id: UUID = UUID.randomUUID(),
    val name: Name,
    val description: Description,
    val type: Type
)
