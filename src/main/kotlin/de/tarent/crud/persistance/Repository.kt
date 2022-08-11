package de.tarent.crud.persistance

import de.tarent.crud.dtos.Group
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
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
            SchemaUtils.create(Groups)
        }
    }

    private val transform = { row: ResultRow -> Group(row[Groups.name], row[Groups.description]) }

    fun insert(group: Group): Boolean = transaction(database) {
        val count = Groups.select { Groups.name eq group.name }.count()

        if (count == 0L) {
            Groups.insert {
                it[name] = group.name
                it[description] = group.description
            }
            true
        } else {
            throw ConflictException("Group already exists!")
        }
    }

    fun update(groupName: String, group: Group): Boolean = transaction(database) {
        val currentGroupExists = Groups.select { Groups.name eq groupName }.count() == 1L
        if (!currentGroupExists) {
            throw NotFoundException("Group '$groupName' does not exist!")
        }

        val newGroupNameExists = if(groupName != group.name) {
            Groups.select { Groups.name eq group.name }.count() == 1L
        } else {
            false
        }
        
        if (newGroupNameExists) {
            throw ConflictException("Group name '${group.name}' already exists!")
        }

        Groups.update({ Groups.name eq groupName }) {
            it[name] = group.name
            it[description] = group.description
        }
        true
    }

    fun load(name: String): Group? = transaction(database) {
        Groups.select { Groups.name eq name }
            .map(transform)
            .firstOrNull()
    }

    fun delete(name: String): Int = transaction(database) {
        Groups.deleteWhere { Groups.name eq name }
    }

    fun list(): List<Group> = transaction(database) {
        Groups.selectAll().map(transform)
    }
}