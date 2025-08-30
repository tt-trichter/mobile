package org.trichter.app.features.runs.data.model

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Run @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val userId: String?,
    val data: RunData,
    val image: String,
    val createdAt: Instant,
    val user: User?
)

@Serializable
data class RunData(
    val duration: Double,
    val rate: Double,
    val volume: Double,
)

@Serializable
data class User(
    val id: String,
    val name: String,
    val username: String
)
