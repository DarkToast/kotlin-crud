package de.tarent.crud.adapters.rest.dtos

import de.tarent.crud.domain.OffsetDateTimeIsoSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class CreateUpdateGroupRequest(
    val name: String,
    val description: String,
)

@Serializable
data class CreateUpdateDeviceRequest(
    val name: String,
    val description: String,
    val type: String,
)

@Serializable
data class CreateMetricRequest(
    val unit: String,
    val value: Double,
    @Serializable(with = OffsetDateTimeIsoSerializer::class)
    val timestamp: OffsetDateTime,
)
