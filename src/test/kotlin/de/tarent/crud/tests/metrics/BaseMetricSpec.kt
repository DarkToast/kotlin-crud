package de.tarent.crud.tests.metrics

import de.tarent.crud.dtos.Metric
import de.tarent.crud.tests.BaseComponentSpec
import de.tarent.crud.tests.asserts.DeviceAssertion
import de.tarent.crud.tests.asserts.MetricAssertion
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import kotlinx.serialization.decodeFromString
import org.assertj.core.api.Assertions.assertThat
import java.time.OffsetDateTime

abstract class BaseMetricSpec : BaseComponentSpec(), MetricAssertion, DeviceAssertion {
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

        assertThat(response.status).isEqualTo(HttpStatusCode.Created)
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
}