package org.trichter.app.features.ble.domain.usecases

import org.trichter.app.features.ble.domain.PermissionsGateway

class OpenAppSettings (
    private val gateway: PermissionsGateway
) {
    operator fun invoke() = gateway.openSettings()
}