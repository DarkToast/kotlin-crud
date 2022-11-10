package de.tarent.crud.tests.groups

import de.tarent.crud.dtos.Group
import de.tarent.crud.tests.BaseComponentSpec
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Assertions

const val DEFAULT_GROUP_NAME = "HWR"

abstract class BaseGroupSpec : BaseComponentSpec() {
    protected suspend fun assertGroup(name: String, description: String, response: HttpResponse): Boolean {
        val group: Group = json.decodeFromString(response.bodyAsText())
        return assertGroup(name, description, group)
    }

    protected fun assertGroup(name: String, description: String, group: Group): Boolean {
        Assertions.assertEquals(name, group.name)
        Assertions.assertEquals(description, group.description)
        return true
    }
}