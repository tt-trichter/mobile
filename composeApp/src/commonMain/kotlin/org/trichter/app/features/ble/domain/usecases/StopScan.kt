package org.trichter.app.features.ble.domain.usecases

import org.trichter.app.features.ble.domain.BleRepository

class StopScan(
    private val repo: BleRepository
) {
    operator fun invoke() = repo.stopScan()
}