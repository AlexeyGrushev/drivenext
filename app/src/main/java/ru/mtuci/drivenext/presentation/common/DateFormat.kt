package ru.mtuci.drivenext.presentation.common

import android.content.res.Resources
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import ru.mtuci.drivenext.R

/** «08:00, 27 сентября 2024» — месяц в родительном падеже берётся из ресурсов. */
fun Resources.formatDateTime(instant: Instant, zone: ZoneId = ZoneId.systemDefault()): String {
    val dt = instant.atZone(zone)
    val month = getStringArray(R.array.months_genitive)[dt.monthValue - 1]
    return String.format("%02d:%02d, %d %s %d", dt.hour, dt.minute, dt.dayOfMonth, month, dt.year)
}

/** «27 сентября 2024». */
fun Resources.formatDate(instant: Instant, zone: ZoneId = ZoneId.systemDefault()): String {
    val d = instant.atZone(zone)
    return formatDate(d.toLocalDate())
}

fun Resources.formatDate(date: LocalDate): String {
    val month = getStringArray(R.array.months_genitive)[date.monthValue - 1]
    return "${date.dayOfMonth} $month ${date.year}"
}

/** «08:00, 27 сентября 2024» для даты аренды (время начала и конца всегда 08:00). */
fun Resources.formatRentalDateTime(date: LocalDate): String =
    String.format("08:00, %s", formatDate(date))

/** «х1 день», «х3 дня», «х5 дней»: склонение по правилам русского языка, не зависит от языка телефона. */
fun Resources.formatDays(days: Int): String {
    val lastTwo = days % 100
    val last = days % 10
    val id = when {
        lastTwo in 11..14 -> R.string.days_many
        last == 1 -> R.string.days_one
        last in 2..4 -> R.string.days_few
        else -> R.string.days_many
    }
    return getString(id, days)
}
