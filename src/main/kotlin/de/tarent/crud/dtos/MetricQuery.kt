package de.tarent.crud.dtos

import java.time.OffsetDateTime

class MetricQuery(
    from: OffsetDateTime? = OffsetDateTime.now().minusHours(6),
    to: OffsetDateTime? = OffsetDateTime.now(),
    val type: String? = null
) {
    val from: OffsetDateTime = from ?: OffsetDateTime.now().minusHours(6)
    val to: OffsetDateTime = to ?: OffsetDateTime.now()
}