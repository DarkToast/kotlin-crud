package de.tarent.crud.adapters.database

import de.tarent.crud.domain.Device
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class DeviceRepository(private val database: Database) {
    fun insert(
        groupName: String,
        device: Device,
    ): String = transaction(database) {
        DeviceEntity.insert {
            it[name] = device.name
            it[description] = device.description
            it[type] = device.type
            it[groupId] = GroupEntity.select(GroupEntity.id).where(GroupEntity.name eq groupName)
            it[DeviceEntity.groupName] = device.groupName
        }

        device.name
    }

    private fun deviceByNameAndGroup(deviceName: String, groupName: String) =
        (DeviceEntity.name eq deviceName) and (DeviceEntity.groupName eq groupName)

    fun update(
        groupName: String,
        deviceName: String,
        device: Device,
    ): Boolean =
        transaction {
            DeviceEntity.update({ deviceByNameAndGroup(deviceName, groupName) }) {
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
            DeviceEntity.selectAll().where {
                (DeviceEntity.name eq deviceName) and (DeviceEntity.groupName eq groupName)
            }.map {
                Device(
                    name = it[DeviceEntity.name],
                    description = it[DeviceEntity.description],
                    type = it[DeviceEntity.type],
                    groupName = it[DeviceEntity.groupName],
                )
            }.firstOrNull()
        }

    fun delete(
        groupName: String,
        deviceName: String,
    ): Int =
        transaction {
            DeviceEntity.deleteWhere { deviceByNameAndGroup(deviceName, groupName) }
        }

    fun findForGroup(groupName: String): List<Device> =
        transaction {
            DeviceEntity.leftJoin(GroupEntity)
                .selectAll()
                .where { GroupEntity.name eq groupName }
                .map {
                    Device(
                        name = it[DeviceEntity.name],
                        description = it[DeviceEntity.description],
                        type = it[DeviceEntity.type],
                        groupName = it[GroupEntity.name],
                    )
                }
        }

    fun exists(
        groupName: String,
        deviceName: String,
    ): Boolean =
        transaction(database) {
            DeviceEntity.selectAll()
                .where { deviceByNameAndGroup(deviceName, groupName) }
                .count() == 1L
        }
}
