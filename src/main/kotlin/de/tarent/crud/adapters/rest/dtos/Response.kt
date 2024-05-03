package de.tarent.crud.adapters.rest.dtos

import de.tarent.crud.domain.Linked

class Response<out P>(val payload: P) : Linked<Response<P>>()