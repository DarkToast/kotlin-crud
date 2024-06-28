package de.tarent.crud.adapters.database

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import org.jetbrains.exposed.sql.Sequence
import org.jetbrains.exposed.sql.javatime.datetime

val groupSeq = Sequence("groupSeq")

object GroupEntity : IdTable<Int>("group") {
    override val id = integer("id").uniqueIndex().autoIncrement(groupSeq.identifier).entityId()
    val name = varchar("name", 50)
    val description = varchar("description", 250)

    init {
        uniqueIndex(name)
    }
}

val deviceSeq = Sequence("deviceSeq")

object DeviceEntity : IdTable<Int>("device") {
    override val id = integer("id").uniqueIndex().autoIncrement(deviceSeq.identifier).entityId()
    val name = varchar("name", 50)
    val description = varchar("description", 250)
    val type = varchar("type", 32)
    val groupId = integer("group_id").references(GroupEntity.id, onDelete = CASCADE)
    val groupName = varchar("groupName", 50)

    init {
        uniqueIndex(name, groupId)
        uniqueIndex(name, groupName)
    }
}

val metricSeq = Sequence("groupSeq")

object MetricEntity : IdTable<Int>("metric") {
    override val id = integer("id").uniqueIndex().autoIncrement(metricSeq.identifier).entityId()
    val unit = varchar("unit", 8)
    val value = decimal("value", 10, 2)
    val timestamp = datetime("timestamp")
    val deviceId = integer("device_id").references(DeviceEntity.id, onDelete = CASCADE)

    init {
        uniqueIndex(timestamp, unit, deviceId)
    }
}
