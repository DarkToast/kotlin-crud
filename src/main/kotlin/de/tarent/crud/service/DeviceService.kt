@file:Suppress("UNUSED_PARAMETER")

package de.tarent.crud.service

import de.tarent.crud.dtos.Device
import de.tarent.crud.persistance.ConflictException
import de.tarent.crud.persistance.DeviceRepository
import de.tarent.crud.persistance.GroupRepository
import de.tarent.crud.persistance.PeristenceException

interface WriteResult<T>
interface ReadResult<T>

sealed interface ReadDeviceResult<T> : ReadResult<T>

data class Ok<T>(val value: T) : WriteResult<T>, ReadResult<T>, ReadDeviceResult<T>
data class GroupDontExists<T>(val groupName: String) : WriteResult<T>, ReadResult<T>, ReadDeviceResult<T>
data class DeviceDontExists<T>(val groupName: String, val deviceName: String) : ReadResult<T>, ReadDeviceResult<T>

data class Failed<T>(val e: PeristenceException) : WriteResult<T>, ReadResult<T>
data class DeviceAlreadyExists<T>(val groupName: String, val deviceName: String) : WriteResult<T>


@Suppress("unused", "RedundantNullableReturnType") // still wip
class DeviceService(private val deviceRepo: DeviceRepository, private val groupRepo: GroupRepository) {
    fun create(groupName: String, device: Device): WriteResult<Pair<String, String>> {
        return if (groupRepo.exists(groupName)) {
            try {
                val deviceName = deviceRepo.insert(groupName, device)
                Ok(Pair(groupName, deviceName))
            } catch (e: PeristenceException) {
                when (e) {
                    is ConflictException -> DeviceAlreadyExists(groupName, device.name)
                    else -> Failed(e)
                }
            }
        } else {
            GroupDontExists(groupName)
        }
    }

    fun read(groupName: String, name: String): ReadDeviceResult<Device> = if (groupRepo.exists(groupName)) {
        deviceRepo.load(groupName, name)
            ?.let { Ok(it) }
            ?: DeviceDontExists(groupName, name)
    } else {
        GroupDontExists(groupName)
    }

    fun update(groupId: String, name: String, device: Device): Boolean = TODO()

    fun delete(groupId: String, name: String): Boolean = TODO()

    fun listDevices(groupName: String): ReadResult<List<Device>> = if (groupRepo.exists(groupName)) {
        Ok(deviceRepo.findForGroup(groupName))
    } else {
        GroupDontExists(groupName)
    }
}

