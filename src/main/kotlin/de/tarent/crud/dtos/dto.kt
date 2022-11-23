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

    override fun addLink(name: String, method: Method, href: URI): Failure {
        require(method == GET) { "Failure only support reading methods "}
        return super.addLink(name, method, href)
    }

    companion object {
        fun onIndex(code: Int, message: String): Failure = Failure(code, message).apply {
            addLink("get_groups", GET, URI("/groups"))
        }

        fun onGroup(code: Int, message: String, groupName: String) = onIndex(code, message).apply {
            addLink("get_group", GET, URI("/groups/$groupName"))
            addLink("get_devices", GET, URI("/groups/$groupName/devices"))
        }
    }
}

@Serializable
data class Device(
    val name: String,
    val description: String,
    val type: String,
) : Linked<Device>() {
    fun withLinks(groupName: String): Device =
        this.addLink("_self", GET, URI("/groups/$groupName/devices/$name"))
            .addLink("update", PUT, URI("/groups/$groupName/devices/$name"))
            .addLink("delete", DELETE, URI("/groups/$groupName/devices/$name"))
            .addLink("get_devices", GET, URI("/groups/$groupName/devices"))
            .addLink("get_group", GET, URI("/groups/$groupName"))
}

@Serializable
data class Group(
    val name: String,
    val description: String
) : Linked<Group>() {
    fun withLinks(): Group =
        this.addLink("_self", GET, URI("/groups/$name"))
            .addLink("index", GET, URI("/"))
            .addLink("delete", DELETE, URI("/groups/$name"))
            .addLink("update", PUT, URI("/groups/$name"))
            .addLink("add_device", POST, URI("/groups/$name/devices"))
            .addLink("list_devices", GET, URI("/groups/$name/devices"))
}
