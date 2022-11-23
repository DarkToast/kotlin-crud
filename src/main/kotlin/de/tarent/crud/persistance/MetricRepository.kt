package de.tarent.crud.persistance

import de.tarent.crud.dtos.Metric
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.ZoneId.systemDefault
import java.util.UUID


class MetricRepository(private val database: Database) {
    fun insert(groupName: String, deviceName: String, metric: Metric) = transaction(database) {
        val deviceIdQuery = DeviceEntity
            .slice(DeviceEntity.id)
            .select { (DeviceEntity.groupId eq groupName) and (DeviceEntity.name eq deviceName) }

        MetricEntity.insert {
            it[id] = metric.id
            it[unit] = metric.unit
            it[value] = BigDecimal.valueOf(metric.value)
            it[timestamp] = metric.timestamp.toLocalDateTime()
            it[deviceId] = deviceIdQuery
        }
    }

    fun load(metricId: UUID): Metric? = transaction(database) {
        MetricEntity
            .select { MetricEntity.id eq metricId }
            .map {
                Metric(
                    id = it[MetricEntity.id].value,
                    unit = it[MetricEntity.unit],
                    value = it[MetricEntity.value].toDouble(),
                    timestamp = it[MetricEntity.timestamp]
                        .atZone(systemDefault())
                        .toOffsetDateTime()
                )
            }
            .firstOrNull()
    }
}