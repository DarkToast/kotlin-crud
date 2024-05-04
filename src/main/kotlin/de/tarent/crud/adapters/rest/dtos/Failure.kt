package de.tarent.crud.adapters.rest.dtos

import de.tarent.crud.adapters.rest.dtos.Method.GET
import kotlinx.serialization.Serializable

@Serializable
data class Failure(
    val code: Int,
    val message: String,
    val cause: String = "",
) : Linked<Failure>() {
    init {
        addLink("index", GET, "/")
    }

    override fun addLink(
        name: String,
        method: Method,
        href: String,
    ): Failure {
        require(method == GET) { "Failure only support reading methods." }
        return super.addLink(name, method, href)
    }

    companion object {
        fun onIndex(
            code: Int,
            message: String,
            cause: String = "",
        ): Failure =
            Failure(code, message, cause)
                .apply {
                    addLink("get_groups", GET, "/groups")
                }

        fun onGroup(
            code: Int,
            message: String,
            cause: String = "",
            groupName: String,
        ) = onIndex(code, message, cause).apply {
            addLink("get_group", GET, "/groups/$groupName")
            addLink("get_devices", GET, "/groups/$groupName/devices")
        }

        fun onDevice(
            code: Int,
            message: String,
            cause: String = "",
            groupName: String,
            deviceName: String,
        ) = onGroup(code, message, cause, groupName).apply {
            addLink("get_device", GET, "/groups/$groupName/devices/$deviceName")
        }
    }
}
