package de.tarent.crud.service

import de.tarent.crud.dtos.Metric
import de.tarent.crud.persistance.DeviceRepository
import de.tarent.crud.persistance.GroupRepository
import de.tarent.crud.service.results.DeviceDontExists
import de.tarent.crud.service.results.GroupDontExists
import de.tarent.crud.service.results.MetricCreateResult
import de.tarent.crud.service.results.MetricDontNotExists
import de.tarent.crud.service.results.MetricReadResult
import de.tarent.crud.service.results.Ok
import java.util.UUID

class MetricService(private val groupRepository: GroupRepository, private val deviceRepository: DeviceRepository) {
    fun create(groupName: String, deviceName: String, metric: Metric): MetricCreateResult<Metric> {
        if (!groupRepository.exists(groupName)) {
            return GroupDontExists(groupName)
        }

        if (!deviceRepository.exists(groupName, deviceName)) {
            return DeviceDontExists(groupName, deviceName)
        }

        return Ok(metric)
    }

    fun read(groupName: String, deviceName: String, metricId: UUID): MetricReadResult<Metric> {
        if (!groupRepository.exists(groupName)) {
            return GroupDontExists(groupName)
        }

        if (!deviceRepository.exists(groupName, deviceName)) {
            return DeviceDontExists(groupName, deviceName)
        }

        return MetricDontNotExists(groupName, deviceName, metricId)
    }
}