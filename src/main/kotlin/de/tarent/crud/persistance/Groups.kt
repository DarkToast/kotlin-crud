package de.tarent.crud.persistance

import org.jetbrains.exposed.sql.Table

object Groups : Table() {
    val name = varchar("name", 50)
    val description = varchar("description", 250)

    override val primaryKey = PrimaryKey(name, name = "GROUP_PK")
}