package de.tarent.crud.persistance

sealed class PeristenceException(msg: String): RuntimeException(msg)

class NotFoundException(msg: String): PeristenceException(msg)
class ConflictException(msg: String): PeristenceException(msg)