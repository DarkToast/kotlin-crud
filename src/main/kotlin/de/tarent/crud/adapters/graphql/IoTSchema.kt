package de.tarent.crud.adapters.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ContactDirective
import com.expediagroup.graphql.server.Schema

@ContactDirective(
    name = "IOT service",
    url = "http://localhost:8080/graphql",
    description = "Managing groups, devices and metrics.",
)
@GraphQLDescription("IOT schema")
class IoTSchema : Schema
