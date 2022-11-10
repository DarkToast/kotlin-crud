package de.tarent.crud.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Group(val name: String, val description: String)