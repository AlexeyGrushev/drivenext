package ru.mtuci.drivenext.domain.usecase

import java.time.LocalDate
import java.time.ZoneId
import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.pricing.RentalQuote
import ru.mtuci.drivenext.domain.repository.BookingRepository

/** Оформляет аренду: считает стоимость и сохраняет бронирование. Время начала и конца — 08:00. */
class CreateBookingUseCase(private val bookingRepository: BookingRepository) {
    suspend operator fun invoke(car: Car, start: LocalDate, end: LocalDate): Result<Long> {
        val zone = ZoneId.systemDefault()
        val quote = RentalQuote.calculate(car.pricePerDay, start, end)
        return bookingRepository.createBooking(
            carId = car.id,
            address = car.address,
            startAt = start.atTime(RENT_HOUR, 0).atZone(zone).toInstant(),
            endAt = end.atTime(RENT_HOUR, 0).atZone(zone).toInstant(),
            quote = quote,
        )
    }

    companion object {
        const val RENT_HOUR = 8
    }
}
