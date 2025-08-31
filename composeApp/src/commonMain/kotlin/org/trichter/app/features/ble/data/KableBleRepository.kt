@file:OptIn(ExperimentalUuidApi::class)

package org.trichter.app.features.ble.data

import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.PlatformAdvertisement
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.characteristicOf
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.trichter.app.features.ble.domain.BleRepository
import org.trichter.app.features.ble.domain.models.TrichterState
import org.trichter.app.features.ble.domain.models.Connection
import org.trichter.app.features.ble.domain.models.ResultMeta
import org.trichter.app.features.ble.domain.models.SessionStatus
import org.trichter.app.util.Log
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class KableBleRepository(
    private val scope: CoroutineScope,
) : BleRepository {

    private val _state = MutableStateFlow(TrichterState())
    override val trichterState: StateFlow<TrichterState> = _state

    private lateinit var peripheral: Peripheral
    private var observeJobs: List<Job> = emptyList()

    private val scanner = Scanner {
        filters { match { name = Filter.Name.Prefix("Trichter") } }
        logging {
            engine = SystemLogEngine
            level = Logging.Level.Events
            format = Logging.Format.Multiline
        }
    }


    private val _scanResults =
        MutableSharedFlow<PlatformAdvertisement>(replay = 0, extraBufferCapacity = 64)
    override val scanResults = _scanResults.asSharedFlow()


    private var scanJob: Job? = null


    override suspend fun startScan() {
        if (scanJob?.isActive == true) return
        scanJob = scope.launch {
            scanner.advertisements
                .onEach { ad -> _scanResults.tryEmit(ad) }
                .collect()
        }
    }


    override fun stopScan() {
        scanJob?.cancel(); scanJob = null
    }


    @OptIn(ExperimentalUuidApi::class)
    override suspend fun connect(advertisement: PlatformAdvertisement): Result<Unit> = runCatching {
        peripheral = Peripheral(advertisement)
        peripheral.connect()

        val s0 = scope.launch {
            peripheral.state.collect { conn ->
                _state.update {
                    it.copy(
                        connection = when (conn) {
                            is State.Connected -> Connection.Connected
                            is State.Connecting -> Connection.Connecting
                            else -> Connection.Disconnected
                        }
                    )
                }
            }
        }

        val s1 = scope.launch {
            peripheral.observe(chStatus).collect { bytes ->
                if (bytes.isNotEmpty()) {
                    Log.d("BLE", "Bytes: $bytes")
                    Log.d("BLE", "First: ${bytes.first()}")
                    Log.d("BLE", "Last: ${bytes.last()}")
                    _state.update { it.copy(status = bytes.first().toSessionStatus()) }
                }
            }
        }

        val s2 = scope.launch {
            peripheral.observe(chResult).collect { bytes ->
                bytes.parseResult()?.let { res ->
                    Log.d("BLE", "Result updated!")
                    _state.update { it.copy(lastResultMeta = res.meta) }
                    // If there is an image, read it (see section 4)
                    if (res.hasImage && res.imageSize > 0) {
                        val img = readImage(peripheral, res.imageSize)
                        _state.update { it.copy(lastImage = img) }
                    }
                }
            }
        }

        observeJobs = listOf(s0, s1, s2)
    }

    override suspend fun disconnect() {
        observeJobs.forEach { it.cancel() }; runCatching { peripheral.disconnect() }
    }

    override suspend fun sendAck() {
        peripheral.write(chControl, byteArrayOf(0x02))
    }

    override suspend fun sendReset() {
        peripheral.write(chControl, byteArrayOf(0x03))
    }

    private suspend fun readImage(peripheral: Peripheral, totalSize: Int): ByteArray {
        require(totalSize >= 0) { "totalSize must be >= 0" }
        val out = ByteArray(totalSize)
        var off = 0
        while (off < totalSize) {
            val chunk = peripheral.read(chImage)
            if (chunk.isEmpty()) break
            val toCopy = minOf(chunk.size, totalSize - off)
            chunk.copyInto(
                destination = out,
                destinationOffset = off,
                startIndex = 0,
                endIndex = toCopy
            )
            off += toCopy
        }
        return if (off == totalSize) out else out.copyOf(off)
    }


    companion object {
        private val TRICHTER_SERVICE_UUID = Uuid.parse("12345678-1234-5678-9ABC-DEF012345678")
        private val TRICHTER_CHAR_STATUS = Uuid.parse("12345678-1234-5678-9ABC-DEF012345679")
        private val TRICHTER_CHAR_RESULT = Uuid.parse("12345678-1234-5678-9ABC-DEF01234567A")
        private val TRICHTER_CHAR_CONTROL = Uuid.parse("12345678-1234-5678-9ABC-DEF01234567B")
        private val TRICHTER_CHAR_IMAGE = Uuid.parse("12345678-1234-5678-9ABC-DEF01234567C")

        private val chStatus = characteristicOf(TRICHTER_SERVICE_UUID, TRICHTER_CHAR_STATUS)
        private val chResult = characteristicOf(TRICHTER_SERVICE_UUID, TRICHTER_CHAR_RESULT)
        private val chControl = characteristicOf(TRICHTER_SERVICE_UUID, TRICHTER_CHAR_CONTROL)
        private val chImage = characteristicOf(TRICHTER_SERVICE_UUID, TRICHTER_CHAR_IMAGE)
    }
}

private fun Byte.toSessionStatus(): SessionStatus = when (this.toInt() and 0xFF) {
    0x00 -> SessionStatus.IDLE
    0x01 -> SessionStatus.WAITING
    0x02 -> SessionStatus.RUNNING
    0x03 -> SessionStatus.COMPLETE
    0x04 -> SessionStatus.ERROR
    else -> SessionStatus.UNKNOWN
}

private fun ByteArray.parseResult(): ParsedResult? {
    if (this.size < 17) return null

    var offset = 0

    fun u32(): UInt {
        val v = (this[offset].toInt() and 0xFF) or
                ((this[offset + 1].toInt() and 0xFF) shl 8) or
                ((this[offset + 2].toInt() and 0xFF) shl 16) or
                ((this[offset + 3].toInt() and 0xFF) shl 24)
        offset += 4
        return v.toUInt()
    }

    fun f32(): Float {
        val bits = u32().toInt()
        return Float.fromBits(bits)
    }

    val durationMs = u32().toLong()
    val rateLpm = f32()
    val volumeL = f32()
    val hasImage = (this[offset++].toInt() and 0xFF) == 1
    val imageSize = u32().toInt()

    return ParsedResult(ResultMeta(durationMs, rateLpm, volumeL, hasImage, imageSize))
}

private data class ParsedResult(val meta: ResultMeta) {
    val hasImage get() = meta.hasImage
    val imageSize get() = meta.imageSize
}

