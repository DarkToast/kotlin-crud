package de.tarent.crud.adapters.rest.dtos

import de.tarent.crud.adapters.rest.dtos.Method.GET
import de.tarent.crud.adapters.rest.dtos.Method.POST
import kotlinx.serialization.Serializable

@Serializable
class Index : Linked<Index>() {
    init {
        addLink("_self", GET, "/")
        addLink("get_groups", GET, "/groups")
        addLink("add_group", POST, "/groups")
        addLink("graphql-API", POST, "/graphql")
        addLink("graphql-Schema", GET, "/graphql/schema")
    }
}
