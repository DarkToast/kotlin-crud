package de.tarent.crud.tests.metrics

import de.tarent.crud.dtos.Failure
import de.tarent.crud.dtos.Metric
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CreateMetricSpec : BaseMetricSpec() {
    private val url = "/groups/$testGroupName/devices/$testDeviceName/metrics"
    private val timestamp: LocalDateTime = now().toLocalDateTime(TimeZone.currentSystemDefault())

    @Test
    fun `create an temperature metric`() = spec.componentSpec {
        // given: A temperature metric
        val body = metricBody("°C", 12.6, timestamp)

        // when: post on metrics
        val response = postMetric(url, body)

        println(response.bodyAsText())
        // then: Status Created
        assertEquals(Created, response.status)
        // and: response is the metric
        assertMetric("°C", 12.6, timestamp, response)
    }

    @Test
    fun `create an pressure metric`() = spec.componentSpec {
        // given: A pressure metric
        val body = metricBody("hPa", 1002.0, timestamp)

        // when: post on metrics
        val response = postMetric(url, body)

        // then: Status Created
        assertEquals(Created, response.status)

        // and: response is the metric
        assertMetric("hPa", 1002.0, timestamp, response)
    }

    @Test
    fun `create an electricity metric`() = spec.componentSpec {
        // given: A watt metric
        val body = metricBody("W", 64.2, timestamp)

        // when: post on metrics
        val response = postMetric(url, body)

        // then: Status Created
        assertEquals(Created, response.status)

        // and: response is the metric
        assertMetric("W", 64.2, timestamp, response)
    }

    @Test
    fun `create metric has further links`() = spec.componentSpec {
        // given: A watt metric
        val body = metricBody("W", 64.2, timestamp)

        // when: post on metrics
        val response = postMetric(url, body)

        // then: Status Created
        assertEquals(Created, response.status)

        val metric: Metric = json.decodeFromString(response.bodyAsText())
        assertMetric("W", 64.2, timestamp, metric)

        // and: It has all related links
        val id = metric.id
        assertLink("_self", "/groups/$testGroupName/devices/$testDeviceName/metrics/$id", "GET", metric.links)
        assertLink("delete", "/groups/$testGroupName/devices/$testDeviceName/metrics/$id", "DELETE", metric.links)
        assertLink("get_device", "/groups/$testGroupName/devices/$testDeviceName", "GET", metric.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", metric.links)
    }

    @Test
    fun `malformed fails`() = spec.componentSpec {
        // given: malformed body
        val body = "{}"

        // when: post on metrics
        val response = postMetric(url, body)

        // then: Status BadRequest
        assertEquals(BadRequest, response.status)

        // and: It has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", failure.links)
        assertLink("get_device", "/groups/$testGroupName/devices/$testDeviceName", "GET", failure.links)
    }

    @Test
    fun `device not found`() = spec.componentSpec {
        // given: a valid body
        val body = metricBody("W", 64.2, timestamp)

        // and: not existing url
        val url = "/groups/$testGroupName/devices/unknown/metrics"

        // when: post on metrics
        val response = postMetric(url, body)

        // then: Status BadRequest
        assertEquals(NotFound, response.status)

        // and: It has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", failure.links)
    }

    @Test
    fun `group not found`() = spec.componentSpec {
        // given: a valid body
        val body = metricBody("W", 64.2, timestamp)

        // and: not existing url
        val url = "/groups/unknown/devices/$testDeviceName/metrics"

        // when: post on metrics
        val response = postMetric(url, body)

        // then: Status BadRequest
        assertEquals(NotFound, response.status)

        // and: It has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_groups", "/groups", "GET", failure.links)
    }


    private suspend fun ApplicationTestBuilder.postMetric(url: String, body: String): HttpResponse =
        client.post(url) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(body)
        }

    private fun metricBody(unit: String, value: Double, datetime: LocalDateTime) =
        """
        | {
        |     "unit": "$unit",
        |     "value": $value,
        |     "timestamp": "$datetime"
        | }
        """.trimMargin("|")
}
