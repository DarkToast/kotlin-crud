@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package de.tarent.crud.dtos

import io.ktor.http.HttpMethod
import kotlinx.serialization.Serializable
import java.net.URI

fun HttpMethod.toString() = this.value

enum class Method {
    POST, GET
}

@Serializable
data class Link(val name: String, val href: String, val method: String)

@Suppress("MemberVisibilityCanBePrivate")
@Serializable
abstract class Linked<out T : Linked<T>>(val links: MutableMap<String, Link> = mutableMapOf()) {
    @Suppress("UNCHECKED_CAST")
    fun addLink(name: String, method: Method, href: URI): T {
        links[name] = Link(name, href.toString(), method.toString())
        return this as T
    }
}