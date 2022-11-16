package de.tarent.crud.dtos

import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
class Index : Linked<Index>()

@Serializable
data class Failure(
    val code: Int,
    val message: String
): Linked<Failure>()

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
): Linked<Group>() {
    init {
        addLink("_self", Method.GET, URI("/groups/$name"))
        addLink("add_device", Method.POST, URI("/groups/$name"))
        addLink("list_devices", Method.GET, URI("/groups/$name/devices"))
    }
}
