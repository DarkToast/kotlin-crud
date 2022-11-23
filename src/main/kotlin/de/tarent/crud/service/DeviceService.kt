@file:Suppress("UNUSED_PARAMETER")

package de.tarent.crud.service

import de.tarent.crud.dtos.Device
import de.tarent.crud.dtos.Group
import de.tarent.crud.persistance.DeviceRepository
import de.tarent.crud.persistance.GroupRepository
import de.tarent.crud.service.results.DeviceAlreadyExists
import de.tarent.crud.service.results.DeviceCreateResult
import de.tarent.crud.service.results.DeviceDeleteResult
import de.tarent.crud.service.results.DeviceDontExists
import de.tarent.crud.service.results.DeviceListResult
import de.tarent.crud.service.results.DeviceReadResult
import de.tarent.crud.service.results.DeviceUpdateResult
import de.tarent.crud.service.results.GroupDontExists
import de.tarent.crud.service.results.Ok

class DeviceService(private val deviceRepo: DeviceRepository, private val groupRepo: GroupRepository) {
    fun create(groupName: String, device: Device): DeviceCreateResult<Device> {
        if (!groupRepo.exists(groupName)) {
            return GroupDontExists(groupName)
        }

        if (deviceRepo.exists(groupName, device.name)) {
            return DeviceAlreadyExists(groupName, device.name)
        }

        deviceRepo.insert(groupName, device)
        return Ok(device)
    }

    fun read(groupName: String, name: String): DeviceReadResult<Device> = if (groupRepo.exists(groupName)) {
        deviceRepo.load(groupName, name)
            ?.let { Ok(it) }
            ?: DeviceDontExists(groupName, name)
    } else {
        GroupDontExists(groupName)
    }

    fun update(groupName: String, deviceName: String, device: Device): DeviceUpdateResult<Device> {
        if (!groupRepo.exists(groupName)) {
            return GroupDontExists(groupName)
        }

        if (!deviceRepo.exists(groupName, deviceName)) {
            return DeviceDontExists(groupName, deviceName)
        }

        if (deviceName != device.name && deviceRepo.exists(groupName, device.name)) {
            return DeviceAlreadyExists(groupName, deviceName)
        }

        deviceRepo.update(groupName, deviceName, device)

        return Ok(device)
    }


    fun delete(groupName: String, deviceName: String): DeviceDeleteResult<Group> = if (groupRepo.exists(groupName)) {
        if (deviceRepo.delete(groupName, deviceName) == 1) {
            groupRepo.load(groupName)?.let { Ok(it) } ?: GroupDontExists(groupName)
        } else {
            DeviceDontExists(groupName, deviceName)
        }
    } else {
        GroupDontExists(groupName)
    }

    fun listDevices(groupName: String): DeviceListResult<List<Device>> = if (groupRepo.exists(groupName)) {
        Ok(deviceRepo.findForGroup(groupName))
    } else {
        GroupDontExists(groupName)
    }
}

