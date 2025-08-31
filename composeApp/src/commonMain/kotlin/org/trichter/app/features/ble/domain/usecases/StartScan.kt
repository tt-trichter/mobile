package org.trichter.app.features.ble.domain.usecases

import org.trichter.app.features.ble.domain.BleRepository

class StartScan(
    private val repo: BleRepository
) {
    suspend operator fun invoke() = repo.startScan()
}