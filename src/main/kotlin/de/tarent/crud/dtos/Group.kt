package de.tarent.crud.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val name: String,
    val description: String
)

@Suppress("unused")
@Serializable
data class Device(
    val name: String,
    val description: String,
    val type: String,
)