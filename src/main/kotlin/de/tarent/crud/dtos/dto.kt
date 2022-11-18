package de.tarent.crud.dtos

import de.tarent.crud.dtos.Method.DELETE
import de.tarent.crud.dtos.Method.GET
import de.tarent.crud.dtos.Method.POST
import de.tarent.crud.dtos.Method.PUT
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
class Index : Linked<Index>() {
    init {
        addLink("_self", GET, URI("/"))
        addLink("get_groups", GET, URI("/groups"))
        addLink("add_group", POST, URI("/groups"))
    }
}

@Serializable
data class Failure(
    val code: Int,
    val message: String
) : Linked<Failure>() {
    init {
        addLink("index", GET, URI("/"))
    }

    companion object {
        fun onIndex(code: Int, message: String): Failure = Failure(code, message).apply {
            addLink("get_groups", GET, URI("/groups"))
            addLink("add_group", POST, URI("/groups"))
        }

        fun onGroup(code: Int, message: String, groupName: String) = onIndex(code, message).apply {
            addLink("get_group", GET, URI("/groups/$groupName"))
        }
    }
}

@Serializable
data class Device(
    val name: String,
    val description: String,
    val type: String,
) : Linked<Group>()

@Serializable
data class Group(
    val name: String,
    val description: String
) : Linked<Group>() {
    init {
        addLink("_self", GET, URI("/groups/$name"))
        addLink("delete", DELETE, URI("/groups/$name"))
        addLink("update", PUT, URI("/groups/$name"))
        addLink("add_device", POST, URI("/groups/$name"))
        addLink("list_devices", GET, URI("/groups/$name/devices"))
    }
}
