package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.model.Booking
import ru.mtuci.drivenext.domain.repository.BookingRepository

class GetBookingUseCase(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(id: Long): Result<Booking> = bookingRepository.getBooking(id)
}
