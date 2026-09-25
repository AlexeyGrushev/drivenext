package ru.mtuci.drivenext.presentation.noconnection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mtuci.drivenext.domain.model.StartDestination
import ru.mtuci.drivenext.domain.usecase.ResolveStartDestinationUseCase

data class NoConnectionUiState(
    val isLoading: Boolean = false,
    val destination: StartDestination? = null,
)

class NoConnectionViewModel(
    private val resolveStartDestination: ResolveStartDestinationUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(NoConnectionUiState())
    val state: StateFlow<NoConnectionUiState> = _state

    fun retry() {
        if (_state.value.isLoading) return
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                delay(600) // чтобы индикатор успел показаться
                resolveStartDestination()
            }
            _state.value = if (result == StartDestination.NoConnection) {
                NoConnectionUiState(isLoading = false) // остаёмся на экране
            } else {
                NoConnectionUiState(isLoading = false, destination = result)
            }
        }
    }
}
