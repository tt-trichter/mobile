package org.trichter.app.features.ble.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String? = null,
    val username: String? = null,
    val displayUsername: String? = null,
)
