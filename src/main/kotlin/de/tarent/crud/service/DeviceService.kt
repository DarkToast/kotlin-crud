@file:Suppress("UNUSED_PARAMETER")

package de.tarent.crud.service

import de.tarent.crud.dtos.Device
import de.tarent.crud.persistance.ConflictException
import de.tarent.crud.persistance.DeviceRepository
import de.tarent.crud.persistance.GroupRepository
import de.tarent.crud.persistance.PeristenceException

sealed class Result
data class Ok(val groupName: String, val deviceName: String) : Result()
data class Failed(val e: PeristenceException) : Result()
data class GroupDontExists(val groupName: String) : Result()
data class DeviceAlreadyExists(val groupName: String, val deviceName: String) : Result()


@Suppress("unused", "RedundantNullableReturnType") // still wip
class DeviceService(private val deviceRepo: DeviceRepository, private val groupRepo: GroupRepository) {
    fun create(groupName: String, device: Device): Result {
        return if (groupRepo.exists(groupName)) {
            try {
                val deviceName = deviceRepo.insert(groupName, device)
                Ok(groupName, deviceName)
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

    fun read(groupId: String, name: String): Device? = TODO()

    fun update(groupId: String, name: String, device: Device): Boolean = TODO()

    fun delete(groupId: String, name: String): Boolean = TODO()

    fun listDevices(groupId: String): List<Device> = TODO()
}

