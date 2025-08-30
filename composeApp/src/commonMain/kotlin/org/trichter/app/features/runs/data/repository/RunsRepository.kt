package org.trichter.app.features.runs.data.repository

import kotlinx.coroutines.flow.Flow
import org.trichter.app.features.runs.data.model.Run

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

interface RunsRepository {
    fun getRuns(): Flow<Result<List<Run>>>;

}
