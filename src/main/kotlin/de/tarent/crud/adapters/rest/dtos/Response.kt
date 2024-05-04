package de.tarent.crud.adapters.rest.dtos

import de.tarent.crud.adapters.rest.dtos.Method.DELETE
import de.tarent.crud.adapters.rest.dtos.Method.GET
import de.tarent.crud.adapters.rest.dtos.Method.POST
import de.tarent.crud.adapters.rest.dtos.Method.PUT
import de.tarent.crud.domain.Group
import kotlinx.serialization.Serializable

@Serializable
abstract class Response<P> : Linked<Response<P>>() {
    abstract val payload: P
}

@Serializable
class GroupResponse(override val payload: Group) : Response<Group>() {
    init {
        val name = payload.name
        this.addLink("_self", GET, "/groups/${name}")
            .addLink("index", GET, "/")
            .addLink("delete", DELETE, "/groups/$name")
            .addLink("update", PUT, "/groups/$name")
            .addLink("add_device", POST, "/groups/$name/devices")
            .addLink("list_devices", GET, "/groups/$name/devices")
    }
}

@Serializable
class GroupListResponse(override val payload: List<Group>) : Response<List<Group>>() {
    init {
        this.addLink("index", GET, "/")
            .addLink("_self", GET, "/groups")
            .addLink("get_group", GET, "/groups/{name}")
    }
}