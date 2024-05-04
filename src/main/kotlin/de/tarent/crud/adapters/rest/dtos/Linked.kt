@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package de.tarent.crud.adapters.rest.dtos

import io.ktor.http.HttpMethod
import kotlinx.serialization.Serializable

fun HttpMethod.toString() = this.value

enum class Method {
    POST,
    GET,
    DELETE,
    PUT,
}

@Serializable
data class Link(val href: String, val method: String)

@Suppress("MemberVisibilityCanBePrivate")
@Serializable
abstract class Linked<out T : Linked<T>>(val links: MutableMap<String, Link> = mutableMapOf()) {
    @Suppress("UNCHECKED_CAST")
    open fun addLink(
        name: String,
        method: Method,
        href: String,
    ): T {
        links[name] = Link(href, method.toString())
        return this as T
    }
}
