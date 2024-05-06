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

class MetricRepository(private val database: Database) {
    fun insert(
        groupName: String,
        deviceName: String,
        metric: Metric,
    ): Metric = transaction(database) {
        val deviceIdQuery =
            DeviceEntity
                .select(DeviceEntity.id)
                .where { 
                    (DeviceEntity.groupId.eqSubQuery(
                        GroupEntity.select(GroupEntity.id)
                            .where { GroupEntity.name eq groupName }) 
                    ) and (DeviceEntity.name eq deviceName)
                }

        val newId = MetricEntity.insert {
            it[unit] = metric.unit
            it[value] = BigDecimal.valueOf(metric.value)
            it[timestamp] = metric.timestamp.toLocalDateTime()
            it[deviceId] = deviceIdQuery
        }[MetricEntity.id].value

        metric.copy(id = newId)
    }

    fun load(
        groupName: String,
        deviceName: String,
        metricId: Int,
    ): Metric? =
        transaction(database) {
            MetricEntity
                .selectAll().where { MetricEntity.id eq metricId }
                .map(transform(groupName, deviceName))
                .firstOrNull()
        }

    fun delete(metricId: Int): Int =
        transaction(database) {
            MetricEntity.deleteWhere { id eq metricId }
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
                    .where { (DeviceEntity.groupId eq 0) and (DeviceEntity.name eq deviceName) }

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
                .map(transform(groupName, deviceName))
                .let { metrics -> MetricList(groupName, deviceName, queryData, metrics) }
        }

    private fun transform(
        groupName: String,
        deviceName: String,
    ) = { row: ResultRow ->
        Metric(
            id = row[MetricEntity.id].value,
            unit = row[MetricEntity.unit],
            value = row[MetricEntity.value].toDouble(),
            timestamp =
                row[MetricEntity.timestamp]
                    .atZone(systemDefault())
                    .toOffsetDateTime(),
            groupName = groupName,
            deviceName = deviceName,
        )
    }
}
