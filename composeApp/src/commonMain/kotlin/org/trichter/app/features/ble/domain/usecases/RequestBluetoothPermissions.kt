package org.trichter.app.features.ble.domain.usecases

import dev.icerock.moko.permissions.PermissionState
import kotlinx.coroutines.flow.Flow
import org.trichter.app.features.ble.domain.PermissionsGateway

class RequestBluetoothPermissions(
    private val gateway: PermissionsGateway
) {
    suspend operator fun invoke() = gateway.requestRequiredPermissions()
}