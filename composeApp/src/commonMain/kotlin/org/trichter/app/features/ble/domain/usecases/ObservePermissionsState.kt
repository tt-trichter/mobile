package org.trichter.app.features.ble.domain.usecases

import dev.icerock.moko.permissions.PermissionState
import kotlinx.coroutines.flow.Flow
import org.trichter.app.features.ble.domain.PermissionsGateway

class ObservePermissionsState(
    private val gateway: PermissionsGateway
) {
    operator fun invoke(): Flow<PermissionState> = gateway.permissionState
}