package de.tarent.crud.dtos

import kotlinx.serialization.Serializable

class Index : Linked<Index>()

data class Failure(val code: Int, val message: String)

@Serializable
data class Device(
    val name: String,
    val description: String,
    val type: String,
)

@Serializable
data class Group(
    val name: String,
    val description: String
)
