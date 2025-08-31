package org.trichter.app.features.ble.domain.models

import com.juul.kable.PlatformAdvertisement
import kotlin.jvm.JvmInline

@JvmInline
value class DeviceId(val value: String) {
    override fun toString(): String = value
}

fun PlatformAdvertisement.id(): DeviceId = DeviceId(this.identifier.toString())