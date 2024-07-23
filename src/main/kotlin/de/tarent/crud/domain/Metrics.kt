package de.tarent.crud.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Serializable
data class Metric(
    val id: Int = -1,
    val unit: String,
    val value: Double,
    @Serializable(with = OffsetDateTimeIsoSerializer::class)
    val timestamp: OffsetDateTime,
    val groupName: String,
    val deviceName: String,
)

/**
 * Using `OffsetDateTime` as response type to give clients the ability to get information of the servers time zone.
 */
@Serializable
class MetricList(
    val groupName: String,
    val deviceName: String,
    @Transient private val query: MetricQuery = MetricQuery(),
    @Transient private var metricList: List<Metric> = emptyList(),
) {
    val metrics: List<Metric> = metricList

    @Serializable(with = OffsetDateTimeIsoSerializer::class)
    val from: OffsetDateTime = query.from.atZone(ZoneId.systemDefault()).toOffsetDateTime()

    @Serializable(with = OffsetDateTimeIsoSerializer::class)
    val to: OffsetDateTime = query.to.atZone(ZoneId.systemDefault()).toOffsetDateTime()

    val type: String? = query.type

    val units: Set<String> = metrics.map { it.unit }.toSet()

    val size: Int = metrics.size
}

/**
 * Using `LocalDateTime` as request type to have a better URL encoding ability.
 */
class MetricQuery(
    from: LocalDateTime? = null,
    to: LocalDateTime? = null,
    val type: String? = null,
) {
    val from: LocalDateTime
    val to: LocalDateTime

    init {
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        this.from = from ?: now.minusHours(6)
        this.to = to ?: now
    }
}
