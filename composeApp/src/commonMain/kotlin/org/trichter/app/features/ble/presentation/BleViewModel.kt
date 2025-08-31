package org.trichter.app.features.ble.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juul.kable.PlatformAdvertisement
import dev.icerock.moko.permissions.PermissionState
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.trichter.app.features.ble.domain.usecases.ConnectToDevice
import org.trichter.app.features.ble.domain.usecases.ObservePermissionsState
import org.trichter.app.features.ble.domain.usecases.OpenAppSettings
import org.trichter.app.features.ble.domain.usecases.RequestBluetoothPermissions
import org.trichter.app.features.ble.domain.usecases.StartScan
import org.trichter.app.features.ble.domain.usecases.StopScan
import org.trichter.app.features.ble.domain.models.ConnectionState
import org.trichter.app.features.ble.domain.models.ResultMeta
import org.trichter.app.features.ble.domain.models.id
import org.trichter.app.features.ble.domain.usecases.DisconnectFromDevice
import org.trichter.app.features.ble.domain.usecases.ObserveScanResults
import org.trichter.app.features.ble.domain.usecases.ObserveTrichterState
import org.trichter.app.features.ble.domain.usecases.SaveRun
import org.trichter.app.features.ble.domain.usecases.SendAck


class BleViewModel(
    observePermissionsStateUseCase: ObservePermissionsState,
    observeScanResultsUseCase: ObserveScanResults,
    observeTrichterStateUseCase: ObserveTrichterState,
    private val requestBluetoothPermissionsUseCase: RequestBluetoothPermissions,
    private val openAppSettingsUseCase: OpenAppSettings,
    private val startScanUseCase: StartScan,
    private val stopScanUseCase: StopScan,
    private val connectUseCase: ConnectToDevice,
    private val disconnectUseCase: DisconnectFromDevice,
    private val sendAckUseCase: SendAck,
    private val saveRunUseCase: SaveRun
) : ViewModel() {


    private var _state = androidx.compose.runtime.mutableStateOf(BleUiState())
    val state: androidx.compose.runtime.State<BleUiState> get() = _state


    init {
        observePermissionsStateUseCase().onEach { ps ->
            _state.value = _state.value.copy(permissionState = ps)
            if (ps == PermissionState.Granted) startScan() else stopScan()
        }.catch { e -> _state.value = _state.value.copy(error = e) }.launchIn(viewModelScope)

        observeScanResultsUseCase().onEach { onAdvertisement(it) }
            .catch { e -> _state.value = _state.value.copy(error = e) }.launchIn(viewModelScope)

        observeTrichterStateUseCase().onEach {
            _state.value = _state.value.copy(trichterState = it)
        }.catch { e -> _state.value = _state.value.copy(error = e) }.launchIn(viewModelScope)


    }


    fun onRequestPermissions() {
        viewModelScope.launch { requestBluetoothPermissionsUseCase() }
    }


    fun onOpenSettings() = openAppSettingsUseCase()

    fun startScan() = viewModelScope.launch { startScanUseCase() }

    fun stopScan() = stopScanUseCase()
    fun sendAck() = viewModelScope.launch { sendAckUseCase() }
    fun saveRun(meta: ResultMeta) = viewModelScope.launch { saveRunUseCase(meta) }


    fun onAdvertisement(advertisement: PlatformAdvertisement) {
        val next = _state.value.advertisements.toMutableMap()
        next[advertisement.id()] = advertisement
        _state.value = _state.value.copy(advertisements = next.toPersistentMap())
    }


    fun connect(advertisement: PlatformAdvertisement) {
        _state.value = _state.value.copy(connectionState = ConnectionState.Connecting, error = null)
        viewModelScope.launch {
            val res = connectUseCase(advertisement)
            _state.value = res.fold(
                onSuccess = { _state.value.copy(connectionState = ConnectionState.Connected) },
                onFailure = { ex ->
                    _state.value.copy(
                        connectionState = ConnectionState.Failed(
                            ex.message ?: "Error"
                        )
                    )
                })
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            disconnectUseCase()
            _state.value = _state.value.copy(connectionState = ConnectionState.Disconnected)
        }
    }


    override fun onCleared() {
        stopScan()
        super.onCleared()
    }
}
