package org.trichter.app.features.ble.presentation

import com.juul.kable.PlatformAdvertisement
import dev.icerock.moko.permissions.PermissionState
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import org.trichter.app.features.ble.domain.models.ConnectionState
import org.trichter.app.features.ble.domain.models.DeviceId
import org.trichter.app.features.ble.domain.models.TrichterState


data class BleUiState(
    val trichterState: TrichterState? = null,
    val permissionState: PermissionState = PermissionState.NotDetermined,
    val advertisements: PersistentMap<DeviceId, PlatformAdvertisement> = persistentMapOf(),
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val error: Throwable? = null,
)