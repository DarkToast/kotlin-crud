package de.tarent.crud.tests.asserts

import de.tarent.crud.adapters.rest.dtos.Response
import de.tarent.crud.domain.Group
import io.ktor.client.statement.HttpResponse
import org.junit.jupiter.api.Assertions.assertEquals

interface GroupAssertion : LinkAssertion {
    suspend fun assertGroup(
        name: String,
        description: String,
        devices: List<String>,
        response: HttpResponse,
    ): Boolean {
        return Assertion.assert<Response<Group>>(response) { assertGroup(name, description, devices, it.payload) }
    }

    fun assertGroup(
        name: String,
        description: String,
        devices: List<String>,
        group: Group,
    ): Boolean {
        assertEquals(name, group.name)
        assertEquals(description, group.description)
        assertEquals(devices, group.devices.map { it.name })
        return true
    }
}
