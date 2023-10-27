package de.tarent.crud.application.results

import java.util.UUID

data class Ok<T>(val value: T) :
    GroupCreateResult<T>,
    GroupReadResult<T>,
    GroupUpdateResult<T>,
    GroupDeleteResult<T>,
    GroupListResult<T>,
    DeviceCreateResult<T>,
    DeviceReadResult<T>,
    DeviceUpdateResult<T>,
    DeviceDeleteResult<T>,
    DeviceListResult<T>,
    MetricCreateResult<T>,
    MetricReadResult<T>,
    MetricDeleteResult<T>,
    MetricQueryResult<T>

data class GroupDontExists<T>(val groupName: String) :
    GroupReadResult<T>,
    GroupUpdateResult<T>,
    GroupDeleteResult<T>,
    DeviceReadResult<T>,
    DeviceCreateResult<T>,
    DeviceUpdateResult<T>,
    DeviceDeleteResult<T>,
    DeviceListResult<T>,
    MetricCreateResult<T>,
    MetricReadResult<T>,
    MetricDeleteResult<T>,
    MetricQueryResult<T>

data class GroupAlreadyExists<T>(val groupName: String) :
    GroupCreateResult<T>,
    GroupUpdateResult<T>

data class DeviceDontExists<T>(val groupName: String, val deviceName: String) :
    DeviceReadResult<T>,
    DeviceUpdateResult<T>,
    DeviceDeleteResult<T>,
    MetricCreateResult<T>,
    MetricReadResult<T>,
    MetricDeleteResult<T>,
    MetricQueryResult<T>

data class DeviceAlreadyExists<T>(val groupName: String, val deviceName: String) :
    DeviceCreateResult<T>,
    DeviceUpdateResult<T>

data class MetricDontNotExists<T>(val groupName: String, val deviceName: String, val metricId: UUID) :
    MetricReadResult<T>,
    MetricDeleteResult<T>
