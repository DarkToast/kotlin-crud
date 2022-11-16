package de.tarent.crud.service

sealed interface CreateGroupResult<T>
sealed interface CreateDeviceResult<T>
sealed interface GroupReadResult<T>
sealed interface DeviceReadResult<T>
sealed interface DeviceUpdateResult<T>
sealed interface GroupUpdateResult<T>
sealed interface DeviceDeleteResult<T>
sealed interface GroupDeleteResult<T>
sealed interface ListDeviceResult<T>
sealed interface ListGroupResult<T>

data class Ok<T>(val value: T) :
    CreateDeviceResult<T>,
    CreateGroupResult<T>,
    DeviceReadResult<T>,
    GroupReadResult<T>,
    DeviceUpdateResult<T>,
    GroupUpdateResult<T>,
    DeviceDeleteResult<T>,
    GroupDeleteResult<T>,
    ListDeviceResult<T>,
    ListGroupResult<T>

data class GroupDontExists<T>(val groupName: String) :
    CreateDeviceResult<T>,
    DeviceReadResult<T>,
    GroupReadResult<T>,
    DeviceUpdateResult<T>,
    GroupUpdateResult<T>,
    DeviceDeleteResult<T>,
    GroupDeleteResult<T>,
    ListDeviceResult<T>

data class GroupAlreadyExists<T>(val groupName: String) :
    CreateGroupResult<T>,
    GroupUpdateResult<T>

data class DeviceDontExists<T>(val groupName: String, val deviceName: String) :
    DeviceReadResult<T>,
    DeviceUpdateResult<T>,
    DeviceDeleteResult<T>

data class DeviceAlreadyExists<T>(val groupName: String, val deviceName: String) :
    CreateDeviceResult<T>,
    DeviceUpdateResult<T>
