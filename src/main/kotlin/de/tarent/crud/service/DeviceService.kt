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
import de.tarent.crud.service.results.DeviceResult
import de.tarent.crud.service.results.DeviceUpdateResult
import de.tarent.crud.service.results.GroupDontExists
import de.tarent.crud.service.results.Ok

typealias ListResult = DeviceListResult<List<Device>>

class DeviceService(private val deviceRepo: DeviceRepository, private val groupRepo: GroupRepository) {
    private inline fun <T, reified R : DeviceResult<T>> groupExists(groupName: String, correct: () -> R): R {
        if (!groupRepo.exists(groupName)) {
            return GroupDontExists<T>(groupName) as R
        }

        return correct()
    }

    fun create(groupName: String, device: Device): DeviceCreateResult<Device> = groupExists(groupName) {
        return if (deviceRepo.exists(groupName, device.name)) {
            DeviceAlreadyExists(groupName, device.name)
        } else {
            deviceRepo.insert(groupName, device)
            Ok(device)
        }
    }

    fun read(groupName: String, name: String): DeviceReadResult<Device> = groupExists(groupName) {
        deviceRepo.load(groupName, name)
            ?.let { Ok(it) }
            ?: DeviceDontExists(groupName, name)
    }

    fun update(groupName: String, deviceName: String, device: Device): DeviceUpdateResult<Device> =
        groupExists(groupName) {
            if (!deviceRepo.exists(groupName, deviceName)) {
                return@groupExists DeviceDontExists(groupName, deviceName)
            }

            if (deviceName != device.name && deviceRepo.exists(groupName, device.name)) {
                return@groupExists DeviceAlreadyExists(groupName, deviceName)
            }

            deviceRepo.update(groupName, deviceName, device)
            Ok(device)
        }

    fun delete(groupName: String, deviceName: String): DeviceDeleteResult<Group> = groupExists(groupName) {
        if (deviceRepo.delete(groupName, deviceName) == 1) {
            groupRepo.load(groupName)?.let { Ok(it) } ?: GroupDontExists(groupName)
        } else {
            DeviceDontExists(groupName, deviceName)
        }
    }

    fun listDevices(groupName: String): ListResult =
        groupExists<List<Device>, ListResult>(groupName) {
            Ok(deviceRepo.findForGroup(groupName))
        }
}
