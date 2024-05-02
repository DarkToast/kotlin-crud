package de.tarent.crud.tests.asserts

import de.tarent.crud.domain.Metric
import de.tarent.crud.tests.asserts.Assertion.assert
import io.ktor.client.statement.HttpResponse
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit.SECONDS

interface MetricAssertion : LinkAssertion {
    suspend fun assertMetric(
        unit: String,
        value: Double,
        timestamp: OffsetDateTime,
        response: HttpResponse,
    ): Boolean {
        return assert<Metric>(response) { assertMetric(unit, value, timestamp, it) }
    }

    fun assertMetric(
        unit: String,
        value: Double,
        timestamp: OffsetDateTime,
        metric: Metric,
    ): Boolean {
        assertEquals(unit, metric.unit)
        assertEquals(value, metric.value)
        assertEquals(timestamp.truncatedTo(SECONDS), metric.timestamp.truncatedTo(SECONDS))
        return true
    }
}
