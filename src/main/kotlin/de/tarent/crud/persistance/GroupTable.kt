package de.tarent.crud.persistance

import org.jetbrains.exposed.sql.Table

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