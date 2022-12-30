package de.tarent.crud.tests.metrics

import de.tarent.crud.domain.Failure
import de.tarent.crud.domain.Metric
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
import kotlinx.serialization.decodeFromString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CreateMetricSpec : BaseMetricSpec() {

    @Test
    fun `create an temperature metric`() = spec.componentSpec {
        // given: A temperature metric
        val body = metricJson("°C", 12.6, timestamp)

        // when: post on metrics
        val response = postMetric(metricsUrl, body)

        // then: Status Created
        assertThat(response.status).isEqualTo(Created)

        // and: response is the metric
        assertMetric("°C", 12.6, timestamp, response)
    }

    @Test
    fun `create an pressure metric`() = spec.componentSpec {
        // given: A pressure metric
        val body = metricJson("hPa", 1002.0, timestamp)

        // when: post on metrics
        val response = postMetric(metricsUrl, body)

        // then: Status Created
        assertThat(response.status).isEqualTo(Created)

        // and: response is the metric
        assertMetric("hPa", 1002.0, timestamp, response)
    }

    @Test
    fun `create an electricity metric`() = spec.componentSpec {
        // given: A watt metric
        val body = metricJson("W", 64.2, timestamp)

        // when: post on metrics
        val response = postMetric(metricsUrl, body)

        // then: Status Created
        assertThat(response.status).isEqualTo(Created)

        // and: response is the metric
        assertMetric("W", 64.2, timestamp, response)
    }

    @Test
    fun `create metric has further links`() = spec.componentSpec {
        // given: A watt metric
        val body = metricJson("W", 64.2, timestamp)

        // when: post on metrics
        val response = postMetric(metricsUrl, body)

        // then: Status Created
        assertThat(response.status).isEqualTo(Created)

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
        val response = postMetric(metricsUrl, body)

        // then: Status BadRequest
        assertThat(response.status).isEqualTo(BadRequest)

        // and: It has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", failure.links)
        assertLink("get_devices", "/groups/$testGroupName/devices", "GET", failure.links)
        assertLink("get_device", "/groups/$testGroupName/devices/$testDeviceName", "GET", failure.links)
    }

    @Test
    fun `device not found`() = spec.componentSpec {
        // given: a valid body
        val body = metricJson("W", 64.2, timestamp)

        // and: not existing url
        val url = "/groups/$testGroupName/devices/unknown/metrics"

        // when: post on metrics
        val response = postMetric(url, body)

        // then: Status BadRequest
        assertThat(response.status).isEqualTo(NotFound)

        // and: It has all related links
        val failure: Failure = json.decodeFromString(response.bodyAsText())
        assertLink("index", "/", "GET", failure.links)
        assertLink("get_group", "/groups/$testGroupName", "GET", failure.links)
    }

    @Test
    fun `group not found`() = spec.componentSpec {
        // given: a valid body
        val body = metricJson("W", 64.2, timestamp)

        // and: not existing url
        val url = "/groups/unknown/devices/$testDeviceName/metrics"

        // when: post on metrics
        val response = postMetric(url, body)

        // then: Status BadRequest
        assertThat(response.status).isEqualTo(NotFound)

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
}
