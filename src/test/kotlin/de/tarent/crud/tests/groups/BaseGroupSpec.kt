package de.tarent.crud.tests.groups

import de.tarent.crud.tests.BaseComponentSpec
import io.ktor.server.testing.ApplicationTestBuilder

const val DEFAULT_GROUP_NAME = "HWR"

abstract class BaseGroupSpec : BaseComponentSpec() {
    protected suspend fun provideExistingDefaultGroup(builder: ApplicationTestBuilder): String =
        createGroup(builder, defaultGroupJson)

    protected val defaultGroupJson = """
      |{
      |  "name" : "$DEFAULT_GROUP_NAME",
      |  "description" : "Hauswirtschaftsraum"
      |}
    """.trimMargin("|")
}