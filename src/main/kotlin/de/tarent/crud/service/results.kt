package de.tarent.crud.service

sealed interface CreateGroupResult<T>
sealed interface CreateDeviceResult<T>
sealed interface ReadResult<T>
sealed interface UpdateResult<T>
sealed interface DeleteResult<T>
sealed interface ListResult<T>

data class Ok<T>(val value: T) :
    CreateDeviceResult<T>,
    CreateGroupResult<T>,
    ReadResult<T>,
    UpdateResult<T>,
    DeleteResult<T>,
    ListResult<T>

data class GroupDontExists<T>(val groupName: String) :
    CreateDeviceResult<T>,
    ReadResult<T>,
    UpdateResult<T>,
    DeleteResult<T>,
    ListResult<T>

data class GroupAlreadyExists<T>(val groupName: String) :
    CreateGroupResult<T>

data class DeviceDontExists<T>(val groupName: String, val deviceName: String) :
    ReadResult<T>,
    UpdateResult<T>,
    DeleteResult<T>

data class DeviceAlreadyExists<T>(val groupName: String, val deviceName: String) :
    CreateDeviceResult<T>,
    UpdateResult<T>
