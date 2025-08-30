package org.trichter.app.features.ble.presentation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.PlatformAdvertisement
import com.juul.kable.Scanner
import com.juul.kable.characteristicOf
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.trichter.app.permissions.PermissionBridge
import org.trichter.app.permissions.PermissionResultCallback
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
private val HEART_RATE_SERVICE = Uuid.parse("0000180D-0000-1000-8000-00805F9B34FB")

@OptIn(ExperimentalUuidApi::class)
private val HEART_RATE_MEASUREMENT = Uuid.parse("00002A37-0000-1000-8000-00805F9B34FB")

class BleViewModel(private val permissionBridge: PermissionBridge, private val preferenceStore: DataStore<Preferences>) : ViewModel() {
    private val _uiState = MutableStateFlow(BleUiState())
    val uiState: StateFlow<BleUiState> = _uiState.asStateFlow()

    private val _isBluetoothPermissionGranted = MutableStateFlow(permissionBridge.isBluetoothPermissionGranted())
    val isBluetoothPermissionGranted: StateFlow<Boolean> = _isBluetoothPermissionGranted.asStateFlow()


    private val lastDeviceIdKey = stringPreferencesKey("lastDeviceId")

    private var hrObservationJob: Job? = null
    private var scanJob: Job? = null
    private val scanner = Scanner {
        filters {
            match { name = Filter.Name.Exact("NimBLE_GATT") }
        }
        logging {
            engine = SystemLogEngine
            level  = Logging.Level.Events
            format = Logging.Format.Multiline
        }
    }

    suspend fun tryReconnectToLast(timeout: Duration = 10.seconds) {
        val lastId = preferenceStore.data.map { it[lastDeviceIdKey] }.first()
        if (lastId.isNullOrBlank()) return

        // don’t start another while one is running
        if (scanJob?.isActive == true) return

        scanJob = viewModelScope.launch {
            _uiState.update { it.copy(isReconnecting = true, error = null) }

            val matched = withTimeoutOrNull(timeout) {
                scanner.advertisements
                    .onEach { ad ->
                        if (ad.identifier.toString() == lastId) {
                            // Found our device → stop scanning and connect
                            stopScan()
                            connect(ad)
                        }
                    }
                    .catch { e -> _uiState.update { it.copy(error = e.message) } }
                    .collect() // will be cancelled by stopScan() once matched
            }

            if (matched == null) {
                // timed out without finding the device
                _uiState.update { it.copy(isReconnecting = false) }
            }
        }
    }

    fun requestBluetoothPermissions() {
        permissionBridge.requestBluetoothPermission(object : PermissionResultCallback {
            override fun onPermissionGranted() {
                _isBluetoothPermissionGranted.value = true
                _uiState.update { it.copy(error = null) }
                startScan()
            }

            override fun onPermissionDenied(isPermanentDenied: Boolean) {
                _isBluetoothPermissionGranted.value = false
                _uiState.update {
                    it.copy(
                        error = if (isPermanentDenied)
                            "Bluetooth permission permanently denied. Enable it in system settings."
                        else
                            "Bluetooth permission denied."
                    )
                }
                stopScan()
            }
        })
    }

    @OptIn(ExperimentalUuidApi::class)
    fun startScan() {
        if (scanJob?.isActive == true) return
        scanJob = viewModelScope.launch {
            scanner.advertisements
                .onEach { ad ->
                    _uiState.update { state ->
                        state.copy(
                            advertisements = state.advertisements + (ad.identifier.toString() to ad),
                        )
                    }
                }
                .catch {e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect()
        }
    }


    @OptIn(ExperimentalUuidApi::class)
    fun connect(advertisement: PlatformAdvertisement) {
        val peripheral = Peripheral(advertisement) {
            logging {
                engine = SystemLogEngine
                level  = Logging.Level.Events
                format = Logging.Format.Multiline
            }
        }
        _uiState.update { it.copy(peripheral = peripheral) }

        viewModelScope.launch {
            try {
                peripheral.connect()

                runCatching {
                    val hrChar = characteristicOf(HEART_RATE_SERVICE, HEART_RATE_MEASUREMENT)
                    val bytes = peripheral.read(hrChar)
                    val bpm = parseHeartRateMeasurement(bytes)
                    _uiState.update { it.copy(heartRateBpm = bpm, lastHrPayload = bytes) }
                }

                // (B) Start observing INDICATIONS for live updates
                startObservingHeartRate(peripheral)

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun startObservingHeartRate(peripheral: Peripheral) {
        hrObservationJob?.cancel()
        hrObservationJob = viewModelScope.launch {
            val hrChar = characteristicOf(HEART_RATE_SERVICE, HEART_RATE_MEASUREMENT)

            peripheral.observe(hrChar)
                .onEach { bytes ->
                    val bpm = parseHeartRateMeasurement(bytes)
                    _uiState.update { it.copy(heartRateBpm = bpm, lastHrPayload = bytes) }
                }
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect()
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
    }

    override fun onCleared() {
        stopScan()
        super.onCleared()
    }
}

data class BleUiState(
    val advertisements: Map<String, PlatformAdvertisement> = emptyMap(),
    val peripheral: Peripheral? = null,
    val error: String? = null,
    val heartRateBpm: Int? = null,
    val isReconnecting: Boolean = false,
    val lastHrPayload: ByteArray? = null
)

data class DeviceItem(
    val id: String,
    val name: String?,
    val rssi: Int?,
    val serviceUuids: List<String>
)

@OptIn(ExperimentalUuidApi::class)
fun PlatformAdvertisement.toDeviceItem(): DeviceItem {
    return DeviceItem(
        id = identifier.toString(),
        name = name,
        rssi = rssi,
        serviceUuids = uuids.map { it.toString() }
    )
}
private fun parseHeartRateMeasurement(payload: ByteArray): Int? {
    if (payload.isEmpty()) return null
    val flags = payload[0].toInt() and 0xFF
    val hr16 = (flags and 0x01) != 0
    return if (hr16) {
        if (payload.size >= 3) {
            val lo = payload[1].toInt() and 0xFF
            val hi = payload[2].toInt() and 0xFF
            (hi shl 8) or lo
        } else null
    } else {
        if (payload.size >= 2) payload[1].toInt() and 0xFF else null
    }
}
