package de.tarent.crud.service

import de.tarent.crud.persistance.PeristenceException

sealed interface CreateResult<T>
sealed interface ReadResult<T>
sealed interface UpdateResult<T>
sealed interface DeleteResult<T>
sealed interface ListResult<T>

data class Ok<T>(val value: T) :
    CreateResult<T>,
    ReadResult<T>,
    UpdateResult<T>,
    DeleteResult<T>,
    ListResult<T>

data class GroupDontExists<T>(val groupName: String) :
    CreateResult<T>,
    ReadResult<T>,
    UpdateResult<T>,
    DeleteResult<T>,
    ListResult<T>

data class DeviceDontExists<T>(val groupName: String, val deviceName: String) :
    ReadResult<T>,
    UpdateResult<T>,
    DeleteResult<T>

data class Failed<T>(val e: PeristenceException): CreateResult<T>

data class DeviceAlreadyExists<T>(val groupName: String, val deviceName: String) :
    CreateResult<T>,
    UpdateResult<T>
