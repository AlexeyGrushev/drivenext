package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.repository.BookingRepository

class CancelBookingUseCase(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> = bookingRepository.cancelBooking(id)
}
