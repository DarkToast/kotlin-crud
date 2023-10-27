package de.tarent.crud.driver.rest

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

@Serializable
data class Failure(
    val code: Int,
    val message: String,
    val cause: String = ""
) : Linked<Failure>() {
    init {
        addLink("index", Method.GET, URI("/"))
    }

    override fun addLink(name: String, method: Method, href: URI): Failure {
        require(method == Method.GET) { "Failure only support reading methods." }
        return super.addLink(name, method, href)
    }

    companion object {
        fun onIndex(code: Int, message: String, cause: String = ""): Failure = Failure(code, message, cause)
            .apply {
                addLink("get_groups", Method.GET, URI("/groups"))
            }

        fun onGroup(code: Int, message: String, cause: String = "", groupName: String) =
            onIndex(code, message, cause).apply {
                addLink("get_group", Method.GET, URI("/groups/$groupName"))
                addLink("get_devices", Method.GET, URI("/groups/$groupName/devices"))
            }

        fun onDevice(code: Int, message: String, cause: String = "", groupName: String, deviceName: String) =
            onGroup(code, message, cause, groupName).apply {
                addLink("get_device", Method.GET, URI("/groups/$groupName/devices/$deviceName"))
            }
    }
}
