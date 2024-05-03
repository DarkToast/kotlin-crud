package de.tarent.crud.adapters.rest.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CreateUpdateGroupRequest(
    val name: String,
    val description: String,
)

@Serializable
data class CreateUpdateDeviceRequest(
    val name: String,
    val description: String,
    val type: String,
)
