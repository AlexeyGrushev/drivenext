package ru.mtuci.drivenext.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.domain.model.Booking
import ru.mtuci.drivenext.domain.repository.Notifier
import ru.mtuci.drivenext.domain.usecase.CancelBookingUseCase
import ru.mtuci.drivenext.domain.usecase.GetBookingUseCase

data class BookingDetailsUiState(
    val isLoading: Boolean = true,
    val booking: Booking? = null,
    val loadFailed: Boolean = false,
    val isCancelling: Boolean = false,
    val cancelled: Boolean = false,
    val cancelError: String? = null,
)

class BookingDetailsViewModel(
    private val getBooking: GetBookingUseCase,
    private val cancelBooking: CancelBookingUseCase,
    private val notifier: Notifier,
) : ViewModel() {

    private val _state = MutableStateFlow(BookingDetailsUiState())
    val state: StateFlow<BookingDetailsUiState> = _state

    private var bookingId: Long? = null

    fun start(id: Long) {
        if (bookingId == id) return
        bookingId = id
        load()
    }

    fun load() {
        val id = bookingId ?: return
        _state.update { it.copy(isLoading = true, loadFailed = false) }
        viewModelScope.launch {
            getBooking(id)
                .onSuccess { b -> _state.update { it.copy(isLoading = false, booking = b) } }
                .onFailure { _state.update { it.copy(isLoading = false, loadFailed = true) } }
        }
    }

    fun cancel() {
        val id = bookingId ?: return
        if (_state.value.isCancelling) return
        _state.update { it.copy(isCancelling = true) }
        viewModelScope.launch {
            cancelBooking(id)
                .onSuccess {
                    _state.value.booking?.let { notifier.bookingCancelled("${it.brand} ${it.model}") }
                    _state.update { it.copy(isCancelling = false, cancelled = true) }
                }
                .onFailure { e -> _state.update { it.copy(isCancelling = false, cancelError = e.message) } }
        }
    }

    fun onErrorShown() {
        _state.update { it.copy(cancelError = null) }
    }
}
