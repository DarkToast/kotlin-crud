package de.tarent.crud.tests.metrics

import de.tarent.crud.dtos.MetricList
import de.tarent.crud.tests.asserts.Assertion
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime.now
import java.time.temporal.ChronoUnit.SECONDS

class QueryMetricSpec : BaseMetricSpec() {

    private val sixHours = now().minusHours(6).minusSeconds(1)
    private val now = now()

    override val spec = super.spec.withSetup {
        createMetric(this, testGroupName, testDeviceName, metricJson("°C", 12.6, now.minusMinutes(25)))
        createMetric(this, testGroupName, testDeviceName, metricJson("°C", 11.6, now.minusMinutes(20)))
        createMetric(this, testGroupName, testDeviceName, metricJson("°C", 10.6, now.minusMinutes(15)))
        createMetric(this, testGroupName, testDeviceName, metricJson("°C", 9.6, now.minusMinutes(10)))
        createMetric(this, testGroupName, testDeviceName, metricJson("°C", 8.6, now.minusMinutes(5)))

        createMetric(this, testGroupName, testDeviceName, metricJson("hPa", 1040.0, now.minusMinutes(25)))
        createMetric(this, testGroupName, testDeviceName, metricJson("hPa", 1030.0, now.minusMinutes(20)))
        createMetric(this, testGroupName, testDeviceName, metricJson("hPa", 1020.0, now.minusMinutes(15)))
        createMetric(this, testGroupName, testDeviceName, metricJson("hPa", 1010.0, now.minusMinutes(10)))
        createMetric(this, testGroupName, testDeviceName, metricJson("hPa", 985.0, now.minusMinutes(5)))
    }

    @Test
    fun `query default`() = spec.componentSpec {
        // when: Get on metrics
        val response = client.get(metricsUrl)

        // then: Status Ok
        assertThat(response.status).isEqualTo(OK)

        // and: The time range is six hours to now.
        // and: The list consists of all metrics.
        Assertion.assert<MetricList>(response) {
            assertThat(it.from).isCloseTo(sixHours, within(2, SECONDS))
            assertThat(it.to).isCloseTo(now, within(2, SECONDS))
            assertThat(it.type).isNull()
            assertThat(it.metrics).hasSize(10)
            true
        }
    }

    @Test
    fun `filter type`() = spec.componentSpec {
        // when: Get on metrics
        var response = client.get("$metricsUrl?type=°C")

        // then: Status Ok
        assertThat(response.status).isEqualTo(OK)

        // and: The time range is six hours to now.
        // and: The list consists only °C
        Assertion.assert<MetricList>(response) {
            assertThat(it.from).isCloseTo(sixHours, within(2, SECONDS))
            assertThat(it.to).isCloseTo(now, within(2, SECONDS))
            assertThat(it.type).isEqualTo("°C")
            assertThat(it.metrics).hasSize(5)
            assertThat(it.metrics).allMatch { metric -> metric.unit == "°C" }
            true
        }

        // when: Get on metrics
        response = client.get("$metricsUrl?type=hPa")

        // then: Status Ok
        assertThat(response.status).isEqualTo(OK)

        // and: The time range is six hours to now.
        // and: The list consists only °C
        Assertion.assert<MetricList>(response) {
            assertThat(it.from).isCloseTo(sixHours, within(2, SECONDS))
            assertThat(it.to).isCloseTo(now, within(2, SECONDS))
            assertThat(it.type).isEqualTo("hPa")
            assertThat(it.metrics).hasSize(5)
            assertThat(it.metrics).allMatch { metric -> metric.unit == "hPa" }
            true
        }
    }

    @Test
    fun `filter last 11 minutes`() = spec.componentSpec {
        // when: Get on metrics
        val response = client.get("$metricsUrl?from=now-11m")

        // then: Status Ok
        assertThat(response.status).isEqualTo(OK)

        // and: The time range is six hours to now.
        // and: The list consists only °C
        Assertion.assert<MetricList>(response) {
            val tenMinutes = now.minusMinutes(11)
            assertThat(it.from).isCloseTo(tenMinutes, within(1, SECONDS))
            assertThat(it.to).isCloseTo(now, within(1, SECONDS))
            assertThat(it.type).isNull()
            assertThat(it.metrics).hasSize(4)
            true
        }
    }

    @Test
    fun `filter last 21 minutes to last 4 minutes`() = spec.componentSpec {
        // when: Get on metrics
        val response = client.get("$metricsUrl?from=now-21m&to=now-4m")

        // then: Status Ok
        assertThat(response.status).isEqualTo(OK)

        // and: The time range is six hours to now.
        // and: The list consists only °C
        Assertion.assert<MetricList>(response) {
            val twentyMinutes = now.minusMinutes(21)
            val fiveMinutes = now.minusMinutes(4)

            assertThat(it.from).isCloseTo(twentyMinutes, within(2, SECONDS))
            assertThat(it.to).isCloseTo(fiveMinutes, within(2, SECONDS))
            assertThat(it.type).isNull()
            assertThat(it.metrics).hasSize(8)
            true
        }
    }

    @Test
    fun combined() = spec.componentSpec {
        // when: Get on metrics
        val response = client.get("$metricsUrl?from=now-21m&to=now-4m&type=hPa")

        // then: Status Ok
        assertThat(response.status).isEqualTo(OK)

        // and: The time range is six hours to now.
        // and: The list consists only °C
        Assertion.assert<MetricList>(response) {
            val twentyMinutes = now.minusMinutes(21)
            val fiveMinutes = now.minusMinutes(4)

            assertThat(it.from).isCloseTo(twentyMinutes, within(1, SECONDS))
            assertThat(it.to).isCloseTo(fiveMinutes, within(1, SECONDS))
            assertThat(it.type).isEqualTo("hPa")
            assertThat(it.metrics).hasSize(4)
            assertThat(it.metrics).allMatch { metric -> metric.unit == "hPa" }
            true
        }
    }

    @Test
    fun `device not found`() = spec.componentSpec {
        // given: not existing url
        val url = "/groups/$testGroupName/devices/unknown/metrics"

        // when: Get on metrics
        val response = client.get(url)

        // then: Status NotFound
        assertThat(response.status).isEqualTo(NotFound)
    }

    @Test
    fun `group not found`() = spec.componentSpec {
        // given: not existing url
        val url = "/groups/unknown/devices/$testDeviceName/metrics"

        // when: Get on metrics
        val response = client.get(url)

        // then: Status NotFound
        assertThat(response.status).isEqualTo(NotFound)
    }
}