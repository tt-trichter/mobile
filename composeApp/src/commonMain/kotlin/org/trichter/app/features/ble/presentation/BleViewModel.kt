package org.trichter.app.features.ble.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juul.kable.PlatformAdvertisement
import dev.icerock.moko.permissions.PermissionState
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.trichter.app.features.ble.domain.usecases.ConnectToDevice
import org.trichter.app.features.ble.domain.usecases.ObservePermissionsState
import org.trichter.app.features.ble.domain.usecases.OpenAppSettings
import org.trichter.app.features.ble.domain.usecases.RequestBluetoothPermissions
import org.trichter.app.features.ble.domain.usecases.StartScan
import org.trichter.app.features.ble.domain.usecases.StopScan
import org.trichter.app.features.ble.domain.models.ConnectionState
import org.trichter.app.features.ble.domain.models.ResultMeta
import org.trichter.app.features.ble.domain.models.UserDto
import org.trichter.app.features.ble.domain.models.id
import org.trichter.app.features.ble.domain.usecases.DisconnectFromDevice
import org.trichter.app.features.ble.domain.usecases.ObserveScanResults
import org.trichter.app.features.ble.domain.usecases.ObserveTrichterState
import org.trichter.app.features.ble.domain.usecases.SaveRun
import org.trichter.app.features.ble.domain.usecases.SearchUsers
import org.trichter.app.features.ble.domain.usecases.SendAck
import kotlin.collections.emptyList


@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
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
    private val saveRunUseCase: SaveRun,
    private val searchUsersUseCase: SearchUsers
) : ViewModel() {


    private var _state = MutableStateFlow(BleUiState())
    val state: StateFlow<BleUiState> get() = _state


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
    private val query = MutableStateFlow("")
    private val _searchUserState = MutableStateFlow(SearchUserState())
    val searchUserState: StateFlow<SearchUserState> = _searchUserState

    init {
        viewModelScope.launch {
            query
                .debounce(300)
                .map { it.trim() }
                .distinctUntilChanged()
                .flatMapLatest { q ->
                    if (q.isBlank()) flowOf(Result.success(emptyList()))
                    else flow { emit(searchUsersUseCase(q)) }
                }
                .onStart { _searchUserState.update { it.copy(loading = true, results = emptyList(), error = null) } }
                .catch { e -> _searchUserState.update { it.copy(loading = false, error = e.message) } }
                .collect { result ->
                    result.fold(
                        onSuccess = { list ->
                            _searchUserState.update { it.copy(loading = false, results = list, error = null) }
                        },
                        onFailure = { e ->
                            _searchUserState.update { it.copy(loading = false, error = e.message) }
                        }
                    )
                }
        }
    }

    fun onQueryChange(newValue: String) {
        _searchUserState.update { it.copy(query = newValue) }
        query.value = newValue
    }
}
data class SearchUserState(
    val query: String = "",
    val loading: Boolean = false,
    val results: List<UserDto> = emptyList(),
    val error: String? = null,
)
