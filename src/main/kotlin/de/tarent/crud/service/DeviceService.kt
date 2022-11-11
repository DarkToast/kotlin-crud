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
sealed interface UpdateDeviceResult<T> : WriteResult<T>

data class Ok<T>(val value: T) :
    WriteResult<T>,
    ReadResult<T>,
    ReadDeviceResult<T>,
    UpdateDeviceResult<T>

data class GroupDontExists<T>(val groupName: String) :
    WriteResult<T>,
    ReadResult<T>,
    ReadDeviceResult<T>,
    UpdateDeviceResult<T>

data class DeviceDontExists<T>(val groupName: String, val deviceName: String) :
    ReadResult<T>,
    ReadDeviceResult<T>,
    UpdateDeviceResult<T>

data class Failed<T>(val e: PeristenceException) :
    WriteResult<T>,
    ReadResult<T>

data class DeviceAlreadyExists<T>(val groupName: String, val deviceName: String) :
    WriteResult<T>,
    UpdateDeviceResult<T>


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

    fun update(groupName: String, deviceName: String, device: Device): UpdateDeviceResult<Device> {
        if (!groupRepo.exists(groupName)) {
            return GroupDontExists(groupName)
        }

        if(!deviceRepo.exists(groupName, deviceName)) {
            return DeviceDontExists(groupName, deviceName)
        }

        if(deviceName != device.name && deviceRepo.exists(groupName, device.name)) {
            return DeviceAlreadyExists(groupName, deviceName)
        }

        deviceRepo.update(groupName, deviceName, device)

        return Ok(device)
    }


    fun delete(groupName: String, deviceName: String): ReadResult<Unit> = if (groupRepo.exists(groupName)) {
        if (deviceRepo.delete(groupName, deviceName) == 1) {
            Ok(Unit)
        } else {
            DeviceDontExists(groupName, deviceName)
        }
    } else {
        GroupDontExists(groupName)
    }

    fun listDevices(groupName: String): ReadResult<List<Device>> = if (groupRepo.exists(groupName)) {
        Ok(deviceRepo.findForGroup(groupName))
    } else {
        GroupDontExists(groupName)
    }
}

