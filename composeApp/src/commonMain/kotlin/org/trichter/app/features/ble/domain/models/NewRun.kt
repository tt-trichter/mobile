package org.trichter.app.features.ble.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewRunDto(
    @SerialName("duration") val durationS: Float,
    @SerialName("rate")     val rateLpm: Float,
    @SerialName("volume")   val volumeL: Float,
    @SerialName("image")    val image: String? = null,
)
