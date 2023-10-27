package de.tarent.crud.domain

data class Name(val value: String) {
    init {
        require(value.length <= 50) { throw DomainException("Name must not be greater than 50 characters.") }
    }

    override fun toString(): String = value
}
