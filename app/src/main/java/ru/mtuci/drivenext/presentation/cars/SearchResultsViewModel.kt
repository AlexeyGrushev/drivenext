package ru.mtuci.drivenext.presentation.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.domain.usecase.SearchCarsUseCase

class SearchResultsViewModel(private val searchCars: SearchCarsUseCase) : ViewModel() {

    private val _state = MutableStateFlow(CarsUiState())
    val state: StateFlow<CarsUiState> = _state

    private var query: String? = null

    /** Запускает поиск один раз для запроса; повторные вызовы с тем же запросом (поворот и т.п.) игнорируются. */
    fun start(newQuery: String) {
        if (query == newQuery) return
        query = newQuery
        search()
    }

    fun retry() = search()

    private fun search() {
        val text = query ?: return
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            searchCars(text)
                .onSuccess { cars -> _state.value = CarsUiState(isLoading = false, cars = cars, isLoaded = true) }
                .onFailure { e ->
                    _state.value = CarsUiState(isLoading = false, errorMessage = e.message, isLoaded = false)
                }
        }
    }
}
