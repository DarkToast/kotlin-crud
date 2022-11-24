package de.tarent.crud.tests.asserts

import de.tarent.crud.dtos.Metric
import io.ktor.client.statement.HttpResponse
import org.junit.jupiter.api.Assertions
import java.time.OffsetDateTime

interface MetricAssertion: LinkAssertion {
    suspend fun assertMetric(unit: String, value: Double, timestamp: OffsetDateTime, response: HttpResponse): Boolean {
        return Assertion.assert<Metric>(response) { assertMetric(unit, value, timestamp, it) }
    }

    fun assertMetric(unit: String, value: Double, timestamp: OffsetDateTime, metric: Metric): Boolean {
        Assertions.assertEquals(unit, metric.unit)
        Assertions.assertEquals(value, metric.value)
        Assertions.assertEquals(timestamp, metric.timestamp)
        return true
    }
}