package de.tarent.crud.driven.database

import de.tarent.crud.domain.Device
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class DeviceRepository(private val database: Database) {
    fun insert(
        groupId: String,
        device: Device,
    ): String =
        transaction(database) {
            DeviceEntity.insert {
                it[id] = device.id
                it[name] = device.name
                it[description] = device.description
                it[type] = device.type
                it[DeviceEntity.groupId] = groupId
            }

            device.name
        }

    fun update(
        groupName: String,
        deviceName: String,
        device: Device,
    ): Boolean =
        transaction {
            DeviceEntity.update({ (DeviceEntity.groupId eq groupName) and (DeviceEntity.name eq deviceName) }) {
                it[name] = device.name
                it[description] = device.description
                it[type] = device.type
            }
            true
        }

    fun load(
        groupName: String,
        deviceName: String,
    ): Device? =
        transaction(database) {
            DeviceEntity
                .select { (DeviceEntity.groupId eq groupName) and (DeviceEntity.name eq deviceName) }
                .map {
                    Device(
                        id = it[DeviceEntity.id].value,
                        name = it[DeviceEntity.name],
                        description = it[DeviceEntity.description],
                        type = it[DeviceEntity.type],
                    )
                }
                .firstOrNull()
        }

    fun delete(
        groupName: String,
        deviceName: String,
    ): Int =
        transaction {
            DeviceEntity.deleteWhere { (groupId eq groupName) and (name eq deviceName) }
        }

    fun findForGroup(groupName: String): List<Device> =
        transaction {
            DeviceEntity
                .select { DeviceEntity.groupId.eq(groupName) }
                .map {
                    Device(
                        id = it[DeviceEntity.id].value,
                        name = it[DeviceEntity.name],
                        description = it[DeviceEntity.description],
                        type = it[DeviceEntity.type],
                    )
                }
        }

    fun exists(
        groupName: String,
        deviceName: String,
    ): Boolean =
        transaction(database) {
            DeviceEntity
                .select { DeviceEntity.name eq deviceName }
                .andWhere { DeviceEntity.groupId eq groupName }
                .count() == 1L
        }
}
