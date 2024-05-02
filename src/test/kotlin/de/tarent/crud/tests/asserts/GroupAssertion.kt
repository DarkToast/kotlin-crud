package de.tarent.crud.tests.asserts

import de.tarent.crud.domain.Group
import io.ktor.client.statement.HttpResponse
import org.junit.jupiter.api.Assertions

interface GroupAssertion : LinkAssertion {
    suspend fun assertGroup(
        name: String,
        description: String,
        response: HttpResponse,
    ): Boolean {
        return Assertion.assert<Group>(response) { assertGroup(name, description, it) }
    }

    fun assertGroup(
        name: String,
        description: String,
        group: Group,
    ): Boolean {
        Assertions.assertEquals(name, group.name)
        Assertions.assertEquals(description, group.description)
        return true
    }
}
