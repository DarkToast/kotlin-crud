package de.tarent.crud.domain

import de.tarent.crud.domain.Method.DELETE
import de.tarent.crud.domain.Method.GET
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.net.URI
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID

class DomainException(message: String) : RuntimeException(message)

@Serializable
data class Metric(
    @Serializable(with = UUIDSerializer::class) val id: UUID = UUID.randomUUID(),
    val unit: String,
    val value: Double,
    @Serializable(with = OffsetDateTimeIsoSerializer::class) val timestamp: OffsetDateTime,
) : Linked<Metric>() {
    fun withLinks(
        groupName: String,
        deviceName: String,
    ): Metric =
        this.addLink("_self", GET, URI("/groups/$groupName/devices/$deviceName/metrics/$id"))
            .addLink("delete", DELETE, URI("/groups/$groupName/devices/$deviceName/metrics/$id"))
            .addLink("get_device", GET, URI("/groups/$groupName/devices/$deviceName"))
            .addLink("get_group", GET, URI("/groups/$groupName"))
}

/**
 * Using `OffsetDateTime` as response type to give clients the ability to get information of the servers time zone.
 */
@Serializable
class MetricList(
    @Transient private val query: MetricQuery = MetricQuery(),
    @Transient private var metricList: List<Metric> = emptyList(),
) : Linked<MetricList>() {
    val metrics: List<Metric> = metricList

    @Serializable(with = OffsetDateTimeIsoSerializer::class)
    val from: OffsetDateTime = query.from.atZone(ZoneId.systemDefault()).toOffsetDateTime()

    @Serializable(with = OffsetDateTimeIsoSerializer::class)
    val to: OffsetDateTime = query.to.atZone(ZoneId.systemDefault()).toOffsetDateTime()

    val type: String? = query.type

    fun withLinks(
        groupName: String,
        deviceName: String,
    ): MetricList {
        val type = if (type != null) "&type=$type" else ""
        val query = "?from=${from.toLocalDateTime()}&to=${to.toLocalDateTime()}$type"

        metricList = metricList.map { it.withLinks(groupName, deviceName) }

        return this.addLink("_self", GET, URI("/groups/$groupName/devices/$deviceName/metrics$query"))
            .addLink("get_device", GET, URI("/groups/$groupName/devices/$deviceName"))
            .addLink("get_group", GET, URI("/groups/$groupName"))
    }
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
