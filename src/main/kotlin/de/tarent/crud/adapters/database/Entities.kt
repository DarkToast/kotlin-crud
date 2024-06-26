package de.tarent.crud.adapters.database

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime

object GroupEntity : UUIDTable("group") {
    val name = varchar("name", 50)
    val description = varchar("description", 250)

    init {
        uniqueIndex(name)
    }
}

object DeviceEntity : UUIDTable("device") {
    val name = varchar("name", 50)
    val description = varchar("description", 250)
    val type = varchar("type", 32)
    val groupId = varchar("group_id", 50) references GroupEntity.name

    init {
        uniqueIndex(name, groupId)
    }
}

@Suppress("unused")
object MetricEntity : UUIDTable("metric") {
    val unit = varchar("unit", 8)
    val value = decimal("value", 10, 2)
    val timestamp = datetime("timestamp")
    val deviceId = uuid("device_id").references(DeviceEntity.id)

    init {
        uniqueIndex(timestamp, unit, deviceId)
    }
}
