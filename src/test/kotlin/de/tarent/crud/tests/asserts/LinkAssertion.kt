package de.tarent.crud.tests.asserts

import de.tarent.crud.dtos.Link
import de.tarent.crud.dtos.Linked
import io.ktor.client.statement.HttpResponse
import org.junit.jupiter.api.Assertions

interface LinkAssertion {
    suspend fun assertLink(name: String, href: String, method: String, response: HttpResponse): Boolean {
        return Assertion.assert<Linked<*>>(response) {
            return assertLink(name, href, method, it.links)
        }
    }

    fun assertLink(name: String, href: String, method: String, links: Map<String, Link>): Boolean {
        Assertions.assertNotNull(links[name], "No link found by $name. Links: $links")

        val link = links[name]
        Assertions.assertEquals(href, link?.href)
        Assertions.assertEquals(method, link?.method)
        return true
    }
}
