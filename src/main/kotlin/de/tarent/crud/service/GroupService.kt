package de.tarent.crud.service

import de.tarent.crud.dtos.Group
import de.tarent.crud.persistance.GroupRepository

class GroupService(private val repo: GroupRepository) {
    fun create(group: Group): GroupCreateResult<Group> = if(repo.exists(group.name)) {
        GroupAlreadyExists(group.name)
    } else {
        repo.insert(group)
        Ok(group)
    }

    fun read(name: String): GroupReadResult<Group> = repo.load(name)
        ?.let { Ok(it) }
        ?: GroupDontExists(name)

    fun update(name: String, group: Group): GroupUpdateResult<Group> {
        if(!repo.exists(name)) {
            return GroupDontExists(name)
        }

        if(name != group.name && repo.exists(group.name)) {
            return GroupAlreadyExists(group.name)
        }

        repo.update(name, group)
        return Ok(group)
    }

    fun delete(name: String): GroupDeleteResult<String> {
        val cnt = repo.delete(name)
        return if (cnt == 1) Ok(name) else GroupDontExists(name)
    }

    fun list(): GroupListResult<List<Group>> = Ok(repo.list())
}

