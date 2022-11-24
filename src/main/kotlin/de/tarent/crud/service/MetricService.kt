package de.tarent.crud.service

import de.tarent.crud.dtos.Device
import de.tarent.crud.dtos.Metric
import de.tarent.crud.persistance.DeviceRepository
import de.tarent.crud.persistance.GroupRepository
import de.tarent.crud.persistance.MetricRepository
import de.tarent.crud.service.results.DeviceDontExists
import de.tarent.crud.service.results.GroupDontExists
import de.tarent.crud.service.results.MetricCreateResult
import de.tarent.crud.service.results.MetricDeleteResult
import de.tarent.crud.service.results.MetricDontNotExists
import de.tarent.crud.service.results.MetricReadResult
import de.tarent.crud.service.results.Ok
import java.util.UUID

class MetricService(
    private val groupRepository: GroupRepository,
    private val deviceRepository: DeviceRepository,
    private val metricRepository: MetricRepository
) {
    fun create(groupName: String, deviceName: String, metric: Metric): MetricCreateResult<Metric> {
        if (!groupRepository.exists(groupName)) {
            return GroupDontExists(groupName)
        }

        if (!deviceRepository.exists(groupName, deviceName)) {
            return DeviceDontExists(groupName, deviceName)
        }

        metricRepository.insert(groupName, deviceName, metric)
        return Ok(metric)
    }

    fun read(groupName: String, deviceName: String, metricId: UUID): MetricReadResult<Metric> {
        if (!groupRepository.exists(groupName)) {
            return GroupDontExists(groupName)
        }

        if (!deviceRepository.exists(groupName, deviceName)) {
            return DeviceDontExists(groupName, deviceName)
        }

        return metricRepository.load(metricId)
            ?.let { Ok(it) }
            ?: MetricDontNotExists(groupName, deviceName, metricId)
    }

    fun delete(groupName: String, deviceName: String, metricId: UUID): MetricDeleteResult<Device> {
        if (!groupRepository.exists(groupName)) {
            return GroupDontExists(groupName)
        }

        if (!deviceRepository.exists(groupName, deviceName)) {
            return DeviceDontExists(groupName, deviceName)
        }

        return if (metricRepository.delete(metricId) == 1) {
            deviceRepository.load(groupName, deviceName)
                ?.let { Ok(it) }
                ?: GroupDontExists(groupName)
        } else {
            MetricDontNotExists(groupName, deviceName, metricId)
        }
    }
}