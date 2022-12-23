package de.tarent.crud.tests.asserts

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object Assertion {
    val json = Json { isLenient = true }
    suspend inline fun <reified T : Any> assert(response: HttpResponse, assertion: (T) -> Boolean): Boolean {
        val element: T = json.decodeFromString(response.bodyAsText())
        return assertion(element)
    }

}