package de.tarent.crud.domain

enum class ActionFoo {
    CREATE,
    READ,
    UPDATE,
    DELETE
}

class Action<T: Action<T>>() {

}