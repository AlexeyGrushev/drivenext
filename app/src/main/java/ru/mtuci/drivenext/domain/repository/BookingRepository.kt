package ru.mtuci.drivenext.domain.repository

import ru.mtuci.drivenext.domain.model.Booking
import ru.mtuci.drivenext.domain.pricing.RentalQuote
import java.time.Instant

interface BookingRepository {
    suspend fun createBooking(
        carId: Long,
        address: String?,
        startAt: Instant,
        endAt: Instant,
        quote: RentalQuote,
    ): Result<Long>

    suspend fun getMyBookings(): Result<List<Booking>>

    /** Бронирование с ФИО водителя и номером ВУ из профиля. */
    suspend fun getBooking(id: Long): Result<Booking>

    suspend fun cancelBooking(id: Long): Result<Unit>
}
