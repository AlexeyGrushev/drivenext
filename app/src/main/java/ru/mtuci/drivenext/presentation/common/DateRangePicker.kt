package ru.mtuci.drivenext.presentation.common

import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import ru.mtuci.drivenext.R

/** Диалог выбора периода аренды (две даты). Прошедшие даты недоступны. */
fun Fragment.showDateRangePicker(start: LocalDate, end: LocalDate, onPicked: (LocalDate, LocalDate) -> Unit) {
    fun LocalDate.toUtcMillis() = atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    fun Long.toLocalDate() = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

    val picker = MaterialDatePicker.Builder.dateRangePicker()
        .setTitleText(R.string.date_range_title)
        .setCalendarConstraints(
            CalendarConstraints.Builder()
                .setStart(LocalDate.now().toUtcMillis())
                .setValidator(DateValidatorPointForward.now())
                .build()
        )
        .setSelection(Pair(start.toUtcMillis(), end.toUtcMillis()))
        .build()
    picker.addOnPositiveButtonClickListener { selection ->
        val first = selection.first ?: return@addOnPositiveButtonClickListener
        val second = selection.second ?: first
        onPicked(first.toLocalDate(), second.toLocalDate())
    }
    picker.show(parentFragmentManager, "date_range_picker")
}
