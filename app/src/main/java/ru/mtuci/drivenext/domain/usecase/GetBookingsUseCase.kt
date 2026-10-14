package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.model.Booking
import ru.mtuci.drivenext.domain.repository.BookingRepository

class GetBookingsUseCase(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(): Result<List<Booking>> = bookingRepository.getMyBookings()
}
