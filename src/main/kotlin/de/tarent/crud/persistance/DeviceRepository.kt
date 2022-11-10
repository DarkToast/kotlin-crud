package de.tarent.crud.persistance

import de.tarent.crud.dtos.Device
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class DeviceRepository(private val database: Database) {
    fun insert(groupId: String, device: Device): String = transaction(database) {
        if (exists(groupId, device.name)) {
            throw ConflictException("device ${device.name} in group $groupId does already exists!")
        }

        DeviceEntity.insert {
            it[this.name] = device.name
            it[this.description] = device.description
            it[this.type] = device.type
            it[this.groupId] = groupId
        }

        device.name
    }

    private fun exists(groupName: String, deviceName: String): Boolean = transaction(database) {
        DeviceEntity
            .select { DeviceEntity.name eq deviceName }
            .andWhere { DeviceEntity.groupId eq groupName }
            .count() == 1L
    }

}