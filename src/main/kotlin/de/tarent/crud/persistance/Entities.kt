package de.tarent.crud.persistance

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object GroupEntity : Table("group") {
    val name = varchar("name", 50)
    val description = varchar("description", 250)

    override val primaryKey = PrimaryKey(name, name = "group_pk")
}

object DeviceEntity : Table("device") {
    val name = varchar("name", 50)
    val description = varchar("description", 250)
    val type = varchar("type", 32)
    val groupId = varchar("group_id", 50) references GroupEntity.name

    override val primaryKey = PrimaryKey(arrayOf(name, groupId), name = "device_pk")
}

@Suppress("unused")
object MetricEntity : UUIDTable("metric") {
    val unit = varchar("unit", 8)
    val value = decimal("value", 10, 2)
    val timestamp = datetime("timestamp")
    val deviceId = varchar("device_id", 50) references DeviceEntity.name

    init {
        uniqueIndex(timestamp, unit, deviceId)
    }
}