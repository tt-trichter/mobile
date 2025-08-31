package org.trichter.app.features.ble.data

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.bluetooth.BLUETOOTH_CONNECT
import dev.icerock.moko.permissions.bluetooth.BLUETOOTH_SCAN
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.trichter.app.features.ble.domain.PermissionsGateway


class MokoPermissionsGateway(
    private val controller: PermissionsController,
    private val scope: CoroutineScope,
) : PermissionsGateway {

    private val _permissionState = MutableStateFlow(PermissionState.NotDetermined)
    override val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()


    init {
        scope.launch(Dispatchers.Main) { refresh() }
    }


    override suspend fun requestRequiredPermissions() {
        try {
            REQUIRED.forEach { controller.providePermission(it) }
            refresh()
        } catch (_: DeniedAlwaysException) {
            _permissionState.value = PermissionState.DeniedAlways
        } catch (_: DeniedException) {
            _permissionState.value = PermissionState.Denied
        }
    }


    override fun openSettings() {
        controller.openAppSettings()
    }


    private suspend fun refresh() {
        val states = REQUIRED.map { controller.getPermissionState(it) }
        _permissionState.value = when {
            states.all { it == PermissionState.Granted } -> PermissionState.Granted
            states.any { it == PermissionState.DeniedAlways } -> PermissionState.DeniedAlways
            states.any { it == PermissionState.Denied } -> PermissionState.Denied
            states.any { it == PermissionState.NotDetermined } -> PermissionState.NotDetermined
            else -> PermissionState.NotDetermined
        }
    }


    companion object {
        val REQUIRED = persistentListOf(
            Permission.BLUETOOTH_SCAN,
            Permission.BLUETOOTH_CONNECT,
        )
    }
}
