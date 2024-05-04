package de.tarent.crud.adapters.database

import de.tarent.crud.domain.Metric
import de.tarent.crud.domain.MetricList
import de.tarent.crud.domain.MetricQuery
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inSubQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.ZoneId.systemDefault
import java.util.UUID

class MetricRepository(private val database: Database) {
    fun insert(
        groupName: String,
        deviceName: String,
        metric: Metric,
    ) = transaction(database) {
        val deviceIdQuery =
            DeviceEntity
                .select(DeviceEntity.id)
                .where { (DeviceEntity.groupId eq groupName) and (DeviceEntity.name eq deviceName) }

        MetricEntity.insert {
            it[id] = metric.id
            it[unit] = metric.unit
            it[value] = BigDecimal.valueOf(metric.value)
            it[timestamp] = metric.timestamp.toLocalDateTime()
            it[deviceId] = deviceIdQuery
        }
    }

    fun load(metricId: UUID): Metric? =
        transaction(database) {
            MetricEntity
                .selectAll().where { MetricEntity.id eq metricId }
                .map(transform)
                .firstOrNull()
        }

    fun delete(metricId: UUID): Int =
        transaction(database) {
            MetricEntity.deleteWhere { MetricEntity.id eq metricId }
        }

    fun query(
        groupName: String,
        deviceName: String,
        queryData: MetricQuery,
    ): MetricList =
        transaction(database) {
            val deviceIdQuery: Query =
                DeviceEntity
                    .select(DeviceEntity.id)
                    .where { (DeviceEntity.groupId eq groupName) and (DeviceEntity.name eq deviceName) }

            val filterDeviceId = MetricEntity.deviceId inSubQuery deviceIdQuery
            val greaterEqFrom = MetricEntity.timestamp greaterEq queryData.from
            val lessEqTo = MetricEntity.timestamp lessEq queryData.to
            val filterUnit = { type: String -> MetricEntity.unit eq type }

            MetricEntity
                .selectAll().where {
                    val expr = filterDeviceId and greaterEqFrom and lessEqTo
                    queryData.type
                        ?.let { expr and filterUnit(it) }
                        ?: expr
                }
                .map(transform)
                .let { metrics -> MetricList(queryData, metrics) }
        }

    private val transform = { row: ResultRow ->
        Metric(
            id = row[MetricEntity.id].value,
            unit = row[MetricEntity.unit],
            value = row[MetricEntity.value].toDouble(),
            timestamp =
                row[MetricEntity.timestamp]
                    .atZone(systemDefault())
                    .toOffsetDateTime(),
        )
    }
}
