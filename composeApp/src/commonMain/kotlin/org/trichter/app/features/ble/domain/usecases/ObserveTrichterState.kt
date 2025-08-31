package org.trichter.app.features.ble.domain.usecases

import kotlinx.coroutines.flow.StateFlow
import org.trichter.app.features.ble.domain.BleRepository
import org.trichter.app.features.ble.domain.models.TrichterState

class ObserveTrichterState (
    private val repo: BleRepository
){
    operator fun invoke(): StateFlow<TrichterState> = repo.trichterState
}