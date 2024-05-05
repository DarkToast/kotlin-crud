package de.tarent.crud.adapters.rest.dtos

import kotlinx.serialization.Serializable

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
