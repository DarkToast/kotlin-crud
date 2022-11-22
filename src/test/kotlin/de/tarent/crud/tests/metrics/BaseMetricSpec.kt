package de.tarent.crud.tests.metrics

import de.tarent.crud.dtos.Metric
import de.tarent.crud.tests.BaseComponentSpec
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Assertions.assertEquals

abstract class BaseMetricSpec : BaseComponentSpec() {
    val testGroupName = "testGroup"
    val testDeviceName = "testDevice"

    protected val spec = Spec().withSetup {
        createGroup(this, testGroupName, "my-test-group")
        createDevice(this, testGroupName, deviceJson(testDeviceName, "test-device", "plug"))
    }

    protected suspend fun assertMetric(
        unit: String,
        value: Double,
        timestamp: LocalDateTime,
        response: HttpResponse
    ): Boolean {
        val metric: Metric = json.decodeFromString(response.bodyAsText())
        return assertMetric(unit, value, timestamp, metric)
    }

    protected fun assertMetric(
        unit: String,
        value: Double,
        timestamp: LocalDateTime,
        metric: Metric
    ): Boolean {
        assertEquals(unit, metric.unit)
        assertEquals(value, metric.value)
        assertEquals(timestamp, metric.timestamp)
        return true
    }
}