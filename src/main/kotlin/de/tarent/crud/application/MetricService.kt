package de.tarent.crud.application

import de.tarent.crud.application.results.DeviceDontExists
import de.tarent.crud.application.results.GroupDontExists
import de.tarent.crud.application.results.MetricCreateResult
import de.tarent.crud.application.results.MetricDeleteResult
import de.tarent.crud.application.results.MetricDontNotExists
import de.tarent.crud.application.results.MetricQueryResult
import de.tarent.crud.application.results.MetricReadResult
import de.tarent.crud.application.results.MetricResult
import de.tarent.crud.application.results.Ok
import de.tarent.crud.domain.Device
import de.tarent.crud.domain.Metric
import de.tarent.crud.domain.MetricList
import de.tarent.crud.domain.MetricQuery
import de.tarent.crud.adapters.database.DeviceRepository
import de.tarent.crud.adapters.database.GroupRepository
import de.tarent.crud.adapters.database.MetricRepository
import java.util.UUID

class MetricService(
    private val groupRepository: GroupRepository,
    private val deviceRepository: DeviceRepository,
    private val metricRepository: MetricRepository,
) {
    private inline fun <T, reified R : MetricResult<T>> check(
        groupName: String,
        deviceName: String,
        correct: () -> R,
    ): R {
        if (!groupRepository.exists(groupName)) {
            return GroupDontExists<T>(groupName) as R
        }

        if (!deviceRepository.exists(groupName, deviceName)) {
            return DeviceDontExists<T>(groupName, deviceName) as R
        }

        return correct()
    }

    fun create(
        groupName: String,
        deviceName: String,
        metric: Metric,
    ): MetricCreateResult<Metric> {
        return check<Metric, MetricCreateResult<Metric>>(groupName, deviceName) {
            metricRepository.insert(groupName, deviceName, metric)
            Ok(metric)
        }
    }

    fun read(
        groupName: String,
        deviceName: String,
        metricId: UUID,
    ): MetricReadResult<Metric> {
        return check(groupName, deviceName) {
            metricRepository.load(metricId)
                ?.let { Ok(it) }
                ?: MetricDontNotExists(groupName, deviceName, metricId)
        }
    }

    fun delete(
        groupName: String,
        deviceName: String,
        metricId: UUID,
    ): MetricDeleteResult<Device> {
        return check(groupName, deviceName) {
            if (metricRepository.delete(metricId) == 1) {
                deviceRepository.load(groupName, deviceName)
                    ?.let { Ok(it) }
                    ?: GroupDontExists(groupName)
            } else {
                MetricDontNotExists(groupName, deviceName, metricId)
            }
        }
    }

    fun query(
        groupName: String,
        deviceName: String,
        query: MetricQuery,
    ): MetricQueryResult<MetricList> {
        return check<MetricList, MetricQueryResult<MetricList>>(groupName, deviceName) {
            Ok(metricRepository.query(groupName, deviceName, query))
        }
    }
}
