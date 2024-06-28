package de.tarent.crud.adapters.database

import de.tarent.crud.domain.Group
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class GroupRepository(private val database: Database) {
    private fun transform(results: List<ResultRow>): Group? {
        if (results.isEmpty()) {
            return null
        }
        return Group(
            name = results[0][GroupEntity.name],
            description = results[0][GroupEntity.description],
            devices = results.mapNotNull { it.getOrNull(DeviceEntity.name) },
        )
    }

    fun insert(group: Group): Boolean =
        transaction(database) {
            GroupEntity.insert {
                it[name] = group.name
                it[description] = group.description
            }
            true
        }

    fun update(
        groupName: String,
        updatedGroup: Group,
    ): Group? =
        transaction(database) {
            GroupEntity.update({ GroupEntity.name eq groupName }) {
                it[name] = updatedGroup.name
                it[description] = updatedGroup.description
            }

            load(updatedGroup.name)
        }

    fun load(name: String): Group? =
        transaction(database) {
            val results =
                GroupEntity
                    .leftJoin(DeviceEntity)
                    .selectAll()
                    .where { GroupEntity.name eq name }
                    .toList()
            transform(results)
        }

    fun delete(name: String): Int =
        transaction(database) {
            GroupEntity.deleteWhere { GroupEntity.name eq name }
        }

    fun list(): List<Group> =
        transaction(database) {
            val resultMap =
                GroupEntity.leftJoin(DeviceEntity)
                    .selectAll()
                    .toList()
                    .groupBy { it[GroupEntity.id].value }

            resultMap
                .mapValues { transform(it.value) }
                .values
                .filterNotNull()
                .toList()
        }

    fun exists(name: String): Boolean =
        transaction(database) {
            GroupEntity.selectAll().where { GroupEntity.name eq name }.count() == 1L
        }
}
