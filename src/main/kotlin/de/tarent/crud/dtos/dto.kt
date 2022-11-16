package de.tarent.crud.dtos

import kotlinx.serialization.Serializable

@Serializable
class Index : Linked<Index>()

@Serializable
data class Failure(
    val code: Int,
    val message: String
)

@Serializable
data class Device(
    val name: String,
    val description: String,
    val type: String,
): Linked<Group>()

@Serializable
data class Group(
    val name: String,
    val description: String
): Linked<Group>()
