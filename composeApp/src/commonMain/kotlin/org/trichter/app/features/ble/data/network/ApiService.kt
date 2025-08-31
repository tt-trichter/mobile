package org.trichter.app.features.ble.data.network

import org.trichter.app.features.ble.domain.models.NewRunDto
import org.trichter.app.features.runs.data.model.Run

interface ApiService {
    suspend fun createRun(newRun: NewRunDto): Result<Unit>
}