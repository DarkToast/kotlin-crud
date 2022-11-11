@file:Suppress("UNUSED_PARAMETER")

package de.tarent.crud.service

import de.tarent.crud.dtos.Device
import de.tarent.crud.persistance.ConflictException
import de.tarent.crud.persistance.DeviceRepository
import de.tarent.crud.persistance.GroupRepository
import de.tarent.crud.persistance.PeristenceException

class DeviceService(private val deviceRepo: DeviceRepository, private val groupRepo: GroupRepository) {
    fun create(groupName: String, device: Device): CreateResult<Pair<String, String>> {
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

    fun read(groupName: String, name: String): ReadResult<Device> = if (groupRepo.exists(groupName)) {
        deviceRepo.load(groupName, name)
            ?.let { Ok(it) }
            ?: DeviceDontExists(groupName, name)
    } else {
        GroupDontExists(groupName)
    }

    fun update(groupName: String, deviceName: String, device: Device): UpdateResult<Device> {
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


    fun delete(groupName: String, deviceName: String): DeleteResult<Unit> = if (groupRepo.exists(groupName)) {
        if (deviceRepo.delete(groupName, deviceName) == 1) {
            Ok(Unit)
        } else {
            DeviceDontExists(groupName, deviceName)
        }
    } else {
        GroupDontExists(groupName)
    }

    fun listDevices(groupName: String): ListResult<List<Device>> = if (groupRepo.exists(groupName)) {
        Ok(deviceRepo.findForGroup(groupName))
    } else {
        GroupDontExists(groupName)
    }
}

