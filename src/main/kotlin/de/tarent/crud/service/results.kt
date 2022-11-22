package de.tarent.crud.service

sealed interface GroupCreateResult<T>
sealed interface GroupReadResult<T>
sealed interface GroupUpdateResult<T>
sealed interface GroupDeleteResult<T>
sealed interface GroupListResult<T>

sealed interface DeviceCreateResult<T>
sealed interface DeviceReadResult<T>
sealed interface DeviceUpdateResult<T>
sealed interface DeviceDeleteResult<T>
sealed interface DeviceListResult<T>

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
    DeviceListResult<T>

data class GroupDontExists<T>(val groupName: String) :
    GroupReadResult<T>,
    GroupUpdateResult<T>,
    GroupDeleteResult<T>,
    DeviceReadResult<T>,
    DeviceCreateResult<T>,
    DeviceUpdateResult<T>,
    DeviceDeleteResult<T>,
    DeviceListResult<T>

data class GroupAlreadyExists<T>(val groupName: String) :
    GroupCreateResult<T>,
    GroupUpdateResult<T>

data class DeviceDontExists<T>(val groupName: String, val deviceName: String) :
    DeviceReadResult<T>,
    DeviceUpdateResult<T>,
    DeviceDeleteResult<T>

data class DeviceAlreadyExists<T>(val groupName: String, val deviceName: String) :
    DeviceCreateResult<T>,
    DeviceUpdateResult<T>
