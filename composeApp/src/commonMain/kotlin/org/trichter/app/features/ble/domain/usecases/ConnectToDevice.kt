package org.trichter.app.features.ble.domain.usecases

import com.juul.kable.PlatformAdvertisement
import org.trichter.app.features.ble.domain.BleRepository

class ConnectToDevice(private val repo: BleRepository) {
    suspend operator fun invoke(advertisement: PlatformAdvertisement): Result<Unit> =
        repo.connect(advertisement)
}
