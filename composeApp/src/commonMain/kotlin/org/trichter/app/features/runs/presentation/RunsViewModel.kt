package org.trichter.app.features.runs.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.trichter.app.features.runs.data.model.Run
import org.trichter.app.features.runs.data.repository.RunsRepository
import org.trichter.app.features.runs.data.repository.Result

data class RunsUiState(
    val runs: List<Run> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RunsViewModel(private val repository: RunsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RunsUiState())
    val uiState: StateFlow<RunsUiState> = _uiState.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            repository.getRuns().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            runs = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.exception.message ?: "Unknown error occurred"
                        )
                    }
                }
            }
        }
    }

    fun retry() {
        loadPosts()
    }
}
