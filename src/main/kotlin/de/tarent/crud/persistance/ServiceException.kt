package de.tarent.crud.persistance

sealed class ServiceException(msg: String): RuntimeException(msg)

class NotFoundException(msg: String): ServiceException(msg)
class ConflictException(msg: String): ServiceException(msg)