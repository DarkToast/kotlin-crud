package de.tarent.crud.tests.metrics

import de.tarent.crud.dtos.Failure
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class ReadMetricSpec : BaseMetricSpec() {
    private lateinit var metricId: UUID

    override val spec = super.spec.withSetup {
        val metric = createMetric(this, testGroupName, testDeviceName, metricJson("°C", 12.6, timestamp))
        metricId = metric.id
    }

    @Test
    fun `get a metric`() = spec.componentSpec {
        // when: Get on metrics
        val response = client.get("$metricsUrl/$metricId")

        // then: Status Ok
        assertEquals(OK, response.status)

        // and: response is the metric
        assertMetric("°C", 12.6, timestamp, response)
    }

    @Test
    fun `not found`() = spec.componentSpec {
        // when: Get on metrics with unknown id
        val response = client.get("$metricsUrl/${UUID.randomUUID()}")

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

        // when: Get on metrics
        val response = client.get(url)

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

        // when: Get on metrics
        val response = client.get(url)

        // then: Status BadRequest
        assertEquals(NotFound, response.status)

        // and: It has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_groups", "/groups", "GET", failure.links)
    }
}