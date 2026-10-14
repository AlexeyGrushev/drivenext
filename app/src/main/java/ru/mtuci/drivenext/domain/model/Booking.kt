package ru.mtuci.drivenext.domain.model

import java.time.Instant

enum class BookingStatus { ACTIVE, CANCELLED }

/** Состояние для списка: активная, завершённая (срок вышел) или отменённая аренда. */
enum class BookingState { ACTIVE, COMPLETED, CANCELLED }

data class Booking(
    val id: Long,
    val carId: Long,
    val brand: String,
    val model: String,
    val imageUrl: String?,
    val address: String?,
    val startAt: Instant,
    val endAt: Instant,
    val days: Int,
    val pricePerDay: Int,
    val insurancePerDay: Int,
    val rentalTotal: Int,
    val insuranceTotal: Int,
    val total: Int,
    val deposit: Int,
    val status: BookingStatus,
    val cancelledAt: Instant?,
    val driverName: String? = null,
    val licenseNumber: String? = null,
) {
    fun stateAt(now: Instant): BookingState = when {
        status == BookingStatus.CANCELLED -> BookingState.CANCELLED
        endAt.isBefore(now) -> BookingState.COMPLETED
        else -> BookingState.ACTIVE
    }
}
