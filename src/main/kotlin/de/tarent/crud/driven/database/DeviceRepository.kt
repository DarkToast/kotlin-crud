package de.tarent.crud.driven.database

import de.tarent.crud.domain.Description
import de.tarent.crud.domain.Device
import de.tarent.crud.domain.Name
import de.tarent.crud.domain.Type
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
    fun insert(groupId: String, device: Device): String = transaction(database) {
        DeviceEntity.insert {
            it[id] = device.id
            it[name] = device.name.toString()
            it[description] = device.description.toString()
            it[type] = device.type.toString()
            it[DeviceEntity.groupId] = groupId
        }

        device.name.toString()
    }

    fun update(groupName: String, deviceName: String, device: Device): Boolean = transaction {
        DeviceEntity.update({ (DeviceEntity.groupId eq groupName) and (DeviceEntity.name eq deviceName) }) {
            it[name] = device.name.toString()
            it[description] = device.description.toString()
            it[type] = device.type.toString()
        }
        true
    }

    fun load(groupName: String, deviceName: String): Device? = transaction(database) {
        DeviceEntity
            .select { (DeviceEntity.groupId eq groupName) and (DeviceEntity.name eq deviceName) }
            .map {
                Device(
                    id = it[DeviceEntity.id].value,
                    name = Name(it[DeviceEntity.name]),
                    description = Description( it[DeviceEntity.description]),
                    type = Type(it[DeviceEntity.type])
                )
            }
            .firstOrNull()
    }

    fun delete(groupName: String, deviceName: String): Int = transaction {
        DeviceEntity.deleteWhere { (groupId eq groupName) and (name eq deviceName) }
    }

    fun findForGroup(groupName: String): List<Device> = transaction {
        DeviceEntity
            .select { DeviceEntity.groupId.eq(groupName) }
            .map {
                Device(
                    id = it[DeviceEntity.id].value,
                    name = Name(it[DeviceEntity.name]),
                    description = Description(it[DeviceEntity.description]),
                    type = Type(it[DeviceEntity.type])
                )
            }
    }

    fun exists(groupName: String, deviceName: String): Boolean = transaction(database) {
        DeviceEntity
            .select { DeviceEntity.name eq deviceName }
            .andWhere { DeviceEntity.groupId eq groupName }
            .count() == 1L
    }
}
