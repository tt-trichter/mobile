package org.trichter.app.features.ble.domain.usecases

import org.trichter.app.features.ble.domain.BleRepository

class DisconnectFromDevice(private val repo: BleRepository) {
    suspend operator fun invoke() = repo.disconnect()
}
