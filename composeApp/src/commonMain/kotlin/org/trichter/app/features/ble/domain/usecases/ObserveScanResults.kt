package org.trichter.app.features.ble.domain.usecases

import com.juul.kable.PlatformAdvertisement
import dev.icerock.moko.permissions.PermissionState
import kotlinx.coroutines.flow.Flow
import org.trichter.app.features.ble.domain.BleRepository
import org.trichter.app.features.ble.domain.PermissionsGateway

class ObserveScanResults(
    private val repo: BleRepository
) {
    operator fun invoke(): Flow<PlatformAdvertisement> = repo.scanResults
}