package org.trichter.app.features.runs.data.network

import org.trichter.app.features.runs.data.model.Run

interface ApiService {
    suspend fun getRuns(): List<Run>
}