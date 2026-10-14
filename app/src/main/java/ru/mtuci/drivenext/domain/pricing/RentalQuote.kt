package ru.mtuci.drivenext.domain.pricing

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Расчёт стоимости аренды: чистая функция без Android, поэтому легко проверяется тестами. */
data class RentalQuote(
    val days: Int,
    val pricePerDay: Int,
    val insurancePerDay: Int,
    val rentalTotal: Int,
    val insuranceTotal: Int,
    val total: Int,
    val deposit: Int,
) {
    companion object {
        const val INSURANCE_PER_DAY = 300
        const val DEPOSIT = 15_000

        /** Срок аренды в днях; минимум 1 день, даже если даты совпали. */
        fun daysBetween(start: LocalDate, end: LocalDate): Int =
            ChronoUnit.DAYS.between(start, end).toInt().coerceAtLeast(1)

        fun calculate(pricePerDay: Int, start: LocalDate, end: LocalDate): RentalQuote {
            val days = daysBetween(start, end)
            val rental = pricePerDay * days
            val insurance = INSURANCE_PER_DAY * days
            return RentalQuote(days, pricePerDay, INSURANCE_PER_DAY, rental, insurance, rental + insurance, DEPOSIT)
        }
    }
}
