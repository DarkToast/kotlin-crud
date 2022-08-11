package de.tarent.crud

import de.tarent.crud.dtos.Group
import de.tarent.crud.persistance.Repository

class GroupService(private val repo: Repository) {
    fun create(group: Group): Boolean = repo.insert(group)

    fun read(name: String): Group? = repo.load(name)

    fun update(name: String, group: Group): Boolean = repo.update(name, group)

    fun delete(name: String): Boolean = repo.delete(name) == 1

    fun listGroups(): List<Group> = repo.list()
}

