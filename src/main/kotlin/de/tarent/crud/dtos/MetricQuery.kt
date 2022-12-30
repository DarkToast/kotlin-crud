package de.tarent.crud.dtos

import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit.SECONDS

class MetricQuery(
    from: OffsetDateTime? = OffsetDateTime.now().minusHours(6).truncatedTo(SECONDS),
    to: OffsetDateTime? = OffsetDateTime.now().truncatedTo(SECONDS),
    val type: String? = null
) {
    val from: OffsetDateTime = (from ?: OffsetDateTime.now().minusHours(6).truncatedTo(SECONDS))
    val to: OffsetDateTime = (to ?: OffsetDateTime.now().truncatedTo(SECONDS))
}