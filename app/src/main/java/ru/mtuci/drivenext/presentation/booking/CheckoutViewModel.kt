package ru.mtuci.drivenext.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.pricing.RentalQuote
import ru.mtuci.drivenext.domain.repository.Notifier
import ru.mtuci.drivenext.domain.usecase.CreateBookingUseCase
import ru.mtuci.drivenext.domain.usecase.GetCarUseCase

data class CheckoutUiState(
    val isLoading: Boolean = true,
    val car: Car? = null,
    val loadFailed: Boolean = false,
    val start: LocalDate = LocalDate.now().plusDays(1),
    val end: LocalDate = LocalDate.now().plusDays(1 + DEFAULT_DAYS),
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val booked: Boolean = false,
) {
    /** Стоимость пересчитывается при каждом изменении дат. */
    val quote: RentalQuote? get() = car?.let { RentalQuote.calculate(it.pricePerDay, start, end) }

    companion object {
        const val DEFAULT_DAYS = 3L
    }
}

class CheckoutViewModel(
    private val getCar: GetCarUseCase,
    private val createBooking: CreateBookingUseCase,
    private val notifier: Notifier,
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutUiState())
    val state: StateFlow<CheckoutUiState> = _state

    private var carId: Long? = null

    fun start(id: Long) {
        if (carId == id) return
        carId = id
        load()
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

    fun setPeriod(start: LocalDate, end: LocalDate) {
        _state.update { it.copy(start = start, end = end) }
    }

    fun submit() {
        val current = _state.value
        val car = current.car ?: return
        if (current.isSubmitting) return
        _state.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            createBooking(car, current.start, current.end)
                .onSuccess {
                    notifier.bookingCreated(car.title, current.start, current.end)
                    _state.update { it.copy(isSubmitting = false, booked = true) }
                }
                .onFailure { e -> _state.update { it.copy(isSubmitting = false, submitError = e.message) } }
        }
    }

    fun onErrorShown() {
        _state.update { it.copy(submitError = null) }
    }
}
