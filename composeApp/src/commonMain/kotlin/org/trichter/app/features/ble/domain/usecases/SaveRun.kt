package org.trichter.app.features.ble.domain.usecases

import org.trichter.app.features.ble.data.network.ApiService
import org.trichter.app.features.ble.domain.models.ResultMeta
import org.trichter.app.features.ble.domain.models.toNewRun
import org.trichter.app.util.Log

class SaveRun(
    private val apiService: ApiService
) {
    suspend operator fun invoke(resultMeta: ResultMeta) {
        apiService.createRun(resultMeta.toNewRun()).fold(
            onSuccess = {Log.i("SAVE", "Run saved successfully")},
            onFailure = { Log.e("SAVE","Failed to save run", it)}
        )
        }

    }
