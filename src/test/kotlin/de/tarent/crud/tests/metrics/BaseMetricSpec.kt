package de.tarent.crud.tests.metrics

import de.tarent.crud.dtos.Metric
import de.tarent.crud.tests.BaseComponentSpec
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.OffsetDateTime

abstract class BaseMetricSpec : BaseComponentSpec() {
    val testGroupName = "testGroup"
    val testDeviceName = "testDevice"

    protected val metricsUrl = "/groups/$testGroupName/devices/$testDeviceName/metrics"
    protected val timestamp: OffsetDateTime = OffsetDateTime.now()

    protected open val spec = Spec().withSetup {
        createGroup(this, testGroupName, "my-test-group")
        createDevice(this, testGroupName, deviceJson(testDeviceName, "test-device", "plug"))
    }

    protected suspend fun createMetric(
        builder: ApplicationTestBuilder,
        groupName: String,
        deviceName: String,
        metricJson: String
    ): Metric {
        val response = builder.client.post("/groups/$groupName/devices/$deviceName/metrics") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(metricJson)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        return json.decodeFromString(response.bodyAsText())
    }

    protected fun metricJson(unit: String, value: Double, datetime: OffsetDateTime) =
        """
        | {
        |     "unit": "$unit",
        |     "value": $value,
        |     "timestamp": "$datetime"
        | }
        """.trimMargin("|")

    protected suspend fun assertMetric(
        unit: String,
        value: Double,
        timestamp: OffsetDateTime,
        response: HttpResponse
    ): Boolean {
        val metric: Metric = json.decodeFromString(response.bodyAsText())
        return assertMetric(unit, value, timestamp, metric)
    }

    protected fun assertMetric(
        unit: String,
        value: Double,
        timestamp: OffsetDateTime,
        metric: Metric
    ): Boolean {
        assertEquals(unit, metric.unit)
        assertEquals(value, metric.value)
        assertEquals(timestamp, metric.timestamp)
        return true
    }
}