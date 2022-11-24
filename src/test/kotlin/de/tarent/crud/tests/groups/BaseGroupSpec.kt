package de.tarent.crud.tests.groups

import de.tarent.crud.tests.BaseComponentSpec
import de.tarent.crud.tests.asserts.GroupAssertion

abstract class BaseGroupSpec : BaseComponentSpec(), GroupAssertion {
    val DEFAULT_GROUP_NAME = "HWR"

    open val spec = Spec().withSetup {
        createGroup(this, DEFAULT_GROUP_NAME, "Hauswirtschaftsraum")
    }
}