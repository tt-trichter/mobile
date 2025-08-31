package org.trichter.app.features.ble.domain.usecases

import org.trichter.app.features.ble.domain.BleRepository

class SendAck (
    private val repo: BleRepository
){
    suspend operator fun invoke() = repo.sendAck()
}