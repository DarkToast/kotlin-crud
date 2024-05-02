package de.tarent.crud.tests

import de.tarent.crud.domain.Device
import de.tarent.crud.domain.Group
import de.tarent.crud.driven.database.DeviceEntity
import de.tarent.crud.driven.database.GroupEntity
import de.tarent.crud.driven.database.MetricEntity
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.java.KoinJavaComponent.inject
import org.jetbrains.exposed.sql.Database as ExposedDatabase

typealias testBlock = suspend ApplicationTestBuilder.() -> Unit

abstract class BaseComponentSpec {
    protected val json = Json { isLenient = true }

    class Spec {
        private val cleanDatabase: testBlock = {
            val db: ExposedDatabase by inject(ExposedDatabase::class.java)
            transaction(db) {
                MetricEntity.deleteAll()
                DeviceEntity.deleteAll()
                GroupEntity.deleteAll()
            }
        }

        private val cleanKoinDi: testBlock = { stopKoin() }

        private val setups: MutableList<testBlock> = mutableListOf()
        private val tearDowns: MutableList<testBlock> = mutableListOf(cleanDatabase, cleanKoinDi)

        @Suppress("unused")
        fun withSetup(setup: testBlock): Spec {
            setups += setup
            return this
        }

        @Suppress("unused")
        fun withTearDown(tearDown: testBlock): Spec {
            tearDowns += tearDown
            return this
        }

        fun componentSpec(block: testBlock) =
            testApplication {
                environment {
                    config = ApplicationConfig("test-application.conf")
                }

                try {
                    setups.forEach { it(this) }
                    block(this)
                } finally {
                    tearDowns.forEach { it(this) }
                }
            }
    }

    protected suspend fun createDevice(
        builder: ApplicationTestBuilder,
        groupName: String,
        deviceJson: String,
    ): String {
        val response =
            builder.client.post("/groups/$groupName/devices") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(deviceJson)
            }

        assertThat(response.status).isEqualTo(Created)
        return json.decodeFromString<Device>(response.bodyAsText()).links["_self"]?.href
            ?: throw IllegalStateException("Illegal creation state. No location header set!")
    }

    protected fun deviceJson(
        name: String,
        description: String,
        type: String,
    ): String =
        """
         |{
         |  "name": "$name",
         |  "description": "$description",
         |  "type": "$type"
         |}
        """.trimMargin("|")

    protected suspend fun createGroup(
        builder: ApplicationTestBuilder,
        groupName: String,
        description: String,
    ): String {
        val response =
            builder.client.post("/groups") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(groupJson(groupName, description))
            }

        assertThat(response.status).isEqualTo(Created)
        val group: Group = json.decodeFromString(response.bodyAsText())

        return group.links["_self"]?.href
            ?: throw IllegalStateException("Illegal creation state. No _self link set!")
    }

    protected fun groupJson(
        name: String,
        description: String,
    ) = """
         |{
         |  "name": "$name",
         |  "description": "$description"
         |}
        """.trimMargin("|")
}
