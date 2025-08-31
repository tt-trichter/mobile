package org.trichter.app.features.ble.domain

import com.juul.kable.PlatformAdvertisement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.trichter.app.features.ble.domain.models.TrichterState

interface BleRepository {
    val scanResults: Flow<PlatformAdvertisement>
    val trichterState: StateFlow<TrichterState>

    suspend fun startScan()
    fun stopScan()

    suspend fun connect(advertisement: PlatformAdvertisement): Result<Unit>
    suspend fun disconnect()
    suspend fun sendAck()
    suspend fun sendReset()
}