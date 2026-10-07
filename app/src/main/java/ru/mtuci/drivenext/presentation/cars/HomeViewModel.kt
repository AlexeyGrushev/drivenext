package ru.mtuci.drivenext.presentation.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.domain.usecase.GetCarsUseCase

class HomeViewModel(private val getCars: GetCarsUseCase) : ViewModel() {

    private val _state = MutableStateFlow(CarsUiState())
    val state: StateFlow<CarsUiState> = _state

    init {
        load()
    }

    /** Загрузка списка с сервера; вызывается при открытии экрана, по «Повторить» и по свайпу вниз. */
    fun load() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            getCars()
                .onSuccess { cars -> _state.value = CarsUiState(isLoading = false, cars = cars, isLoaded = true) }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message, isLoaded = false) }
                }
        }
    }
}
