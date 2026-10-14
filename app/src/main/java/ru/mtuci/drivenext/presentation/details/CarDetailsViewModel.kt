package ru.mtuci.drivenext.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.usecase.GetCarUseCase
import ru.mtuci.drivenext.domain.usecase.ObserveIsFavoriteUseCase
import ru.mtuci.drivenext.domain.usecase.ToggleFavoriteUseCase

data class CarDetailsUiState(
    val isLoading: Boolean = true,
    val car: Car? = null,
    val loadFailed: Boolean = false,
    val isFavorite: Boolean = false,
    val favoriteError: String? = null,
)

class CarDetailsViewModel(
    private val getCar: GetCarUseCase,
    private val observeIsFavorite: ObserveIsFavoriteUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CarDetailsUiState())
    val state: StateFlow<CarDetailsUiState> = _state

    private var carId: Long? = null

    /** Запускается один раз для автомобиля; повторные вызовы (поворот и т.п.) игнорируются. */
    fun start(id: Long) {
        if (carId == id) return
        carId = id
        load()
        // Room присылает новое значение при любом изменении таблицы избранного
        viewModelScope.launch {
            observeIsFavorite(id).collect { favorite -> _state.update { it.copy(isFavorite = favorite) } }
        }
    }

    fun load() {
        val id = carId ?: return
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        viewModelScope.launch {
            getCar(id)
                .onSuccess { car -> _state.update { it.copy(isLoading = false, car = car) } }
                .onFailure { _state.update { it.copy(isLoading = false, loadFailed = true) } }
        }
    }

    fun onFavoriteClick() {
        val car = _state.value.car ?: return
        viewModelScope.launch {
            toggleFavorite(car).onFailure { e -> _state.update { it.copy(favoriteError = e.message) } }
        }
    }

    fun onFavoriteErrorShown() {
        _state.update { it.copy(favoriteError = null) }
    }
}
