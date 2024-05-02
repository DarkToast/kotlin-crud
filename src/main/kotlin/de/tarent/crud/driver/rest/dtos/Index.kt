package de.tarent.crud.driver.rest.dtos

import de.tarent.crud.domain.Linked
import de.tarent.crud.domain.Method
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
class Index : Linked<Index>() {
    init {
        addLink("_self", Method.GET, URI("/"))
        addLink("get_groups", Method.GET, URI("/groups"))
        addLink("add_group", Method.POST, URI("/groups"))
    }
}