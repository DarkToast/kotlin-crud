package de.tarent.crud.driven.database

import de.tarent.crud.domain.Description
import de.tarent.crud.domain.Group
import de.tarent.crud.domain.Name
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class GroupRepository(private val database: Database) {
    private val transform = { row: ResultRow ->
        Group(
            row[GroupEntity.id].value,
            Name(row[GroupEntity.name]),
            Description(row[GroupEntity.description])
        )
    }

    fun insert(group: Group): Boolean = transaction(database) {
        GroupEntity.insert {
            it[name] = group.name.toString()
            it[description] = group.description.toString()
        }
        true
    }

    fun update(groupName: String, updatedGroup: Group): Boolean = transaction(database) {
        GroupEntity.update({ GroupEntity.name eq groupName }) {
            it[name] = updatedGroup.name.toString()
            it[description] = updatedGroup.description.toString()
        }
        true
    }

    fun load(name: String): Group? = transaction(database) {
        GroupEntity.select { GroupEntity.name eq name }
            .map(transform)
            .firstOrNull()
    }

    fun delete(name: String): Int = transaction(database) {
        GroupEntity.deleteWhere { GroupEntity.name eq name }
    }

    fun list(): List<Group> = transaction(database) {
        GroupEntity.selectAll().map(transform)
    }

    fun exists(name: String): Boolean = transaction(database) {
        GroupEntity.select { GroupEntity.name eq name }.count() == 1L
    }
}
