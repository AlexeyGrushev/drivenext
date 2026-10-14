package ru.mtuci.drivenext.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.domain.model.Booking
import ru.mtuci.drivenext.domain.usecase.GetBookingsUseCase

data class BookingsUiState(
    val isLoading: Boolean = true,
    val bookings: List<Booking> = emptyList(),
    val isLoaded: Boolean = false,
    val loadFailed: Boolean = false,
)

class BookingsViewModel(private val getBookings: GetBookingsUseCase) : ViewModel() {

    private val _state = MutableStateFlow(BookingsUiState())
    val state: StateFlow<BookingsUiState> = _state

    /** Вызывается при каждом открытии экрана: после отмены брони список должен обновиться. */
    fun load() {
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        viewModelScope.launch {
            getBookings()
                .onSuccess { list -> _state.value = BookingsUiState(isLoading = false, bookings = list, isLoaded = true) }
                .onFailure { _state.update { it.copy(isLoading = false, loadFailed = true) } }
        }
    }
}
