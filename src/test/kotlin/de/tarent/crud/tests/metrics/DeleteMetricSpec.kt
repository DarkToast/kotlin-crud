package de.tarent.crud.tests.metrics

import de.tarent.crud.dtos.Device
import de.tarent.crud.dtos.Failure
import io.ktor.client.request.delete
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class DeleteMetricSpec : BaseMetricSpec() {
    private lateinit var metricId: UUID

    override val spec = super.spec.withSetup {
        val metric = createMetric(this, testGroupName, testDeviceName, metricJson("°C", 12.6, timestamp))
        metricId = metric.id
    }

    @Test
    fun `delete a metric`() = spec.componentSpec {
        // when: Delete on metrics
        val response = client.delete("$metricsUrl/$metricId")

        // then: Status Ok
        assertEquals(OK, response.status)

        val device: Device = json.decodeFromString(response.bodyAsText())
        assertDevice(testDeviceName, "test-device", "plug", device)
    }

    @Test
    fun `not found`() = spec.componentSpec {
        // when: Delete on metrics with unknown id
        val response = client.delete("$metricsUrl/${UUID.randomUUID()}")

        // then: Status Ok
        assertEquals(NotFound, response.status)

        // and: It has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", failure.links)
        assertLink("get_device", "/groups/$testGroupName/devices/$testDeviceName", "GET", failure.links)
    }

    @Test
    fun `device not found`() = spec.componentSpec {
        // given: not existing url
        val url = "/groups/$testGroupName/devices/unknown/metrics/$metricId"

        // when: Delete on metrics
        val response = client.delete(url)

        // then: Status BadRequest
        assertEquals(NotFound, response.status)

        // and: It has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_groups", "/groups", "GET", failure.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", failure.links)
    }

    @Test
    fun `group not found`() = spec.componentSpec {
        // given: not existing url
        val url = "/groups/unknown/devices/$testDeviceName/metrics/$metricId"

        // when: Delete on metrics
        val response = client.delete(url)

        // then: Status BadRequest
        assertEquals(NotFound, response.status)

        // and: It has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_groups", "/groups", "GET", failure.links)
    }
}
