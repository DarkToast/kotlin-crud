package de.tarent.crud.persistance

import de.tarent.crud.dtos.Group
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class Repository(private val database: Database) {
    init {
        transaction(database) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(GroupEntity, DeviceEntity)
        }
    }

    private val transform = { row: ResultRow ->
        Group(row[GroupEntity.name], row[GroupEntity.description])
    }

    fun insert(group: Group): Boolean = transaction(database) {
        val count = GroupEntity.select { GroupEntity.name eq group.name }.count()

        if (count == 0L) {
            GroupEntity.insert {
                it[name] = group.name
                it[description] = group.description
            }
            true
        } else {
            throw ConflictException("Group already exists!")
        }
    }

    fun update(groupName: String, updatedGroup: Group): Boolean = transaction(database) {
        val currentGroupExists = GroupEntity.select { GroupEntity.name eq groupName }.count() == 1L
        if (!currentGroupExists) {
            throw NotFoundException("Group '$groupName' does not exist!")
        }

        val newGroupNameExists = if(groupName != updatedGroup.name) {
            GroupEntity.select { GroupEntity.name eq updatedGroup.name }.count() == 1L
        } else {
            false
        }
        
        if (newGroupNameExists) {
            throw ConflictException("Group name '${updatedGroup.name}' already exists!")
        }

        GroupEntity.update({ GroupEntity.name eq groupName }) {
            it[name] = updatedGroup.name
            it[description] = updatedGroup.description
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
}