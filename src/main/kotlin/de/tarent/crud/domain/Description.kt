package de.tarent.crud.domain

data class Description(val value: String) {
    init {
        require(value.length <= 250) { throw DomainException("Description must not be greater than 50 characters.") }
    }

    override fun toString(): String = value
}