package de.tarent.crud.tests.groups

import de.tarent.crud.tests.BaseComponentSpec

const val DEFAULT_GROUP_NAME = "HWR"

abstract class BaseGroupSpec : BaseComponentSpec() {
    open val spec = Spec().withSetup {
        createGroup(this, DEFAULT_GROUP_NAME, "Hauswirtschaftsraum")
    }
}