package org.trichter.app.features.ble.domain

import dev.icerock.moko.permissions.PermissionState
import kotlinx.coroutines.flow.Flow

interface PermissionsGateway {
    val permissionState: Flow<PermissionState>

    suspend fun requestRequiredPermissions()

    fun openSettings()
}