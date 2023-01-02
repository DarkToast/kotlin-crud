package de.tarent.crud.domain

data class Type(val value: String) {
    init {
        require(value.length <= 32) { throw DomainException("Type must not be greater than 32 characters.") }
    }

    override fun toString(): String = value
}