package ru.mtuci.drivenext.presentation.common

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.domain.validation.Validators

/** Подставляет «/» по ходу ввода: 12122000 -> 12/12/2000. */
class DateMaskWatcher : TextWatcher {
    private var editing = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(s: Editable) {
        if (editing) return
        editing = true
        val digits = s.filter { it.isDigit() }.take(MAX_DIGITS)
        val formatted = buildString {
            digits.forEachIndexed { index, char ->
                if (index == 2 || index == 4) append('/')
                append(char)
            }
        }
        if (formatted != s.toString()) {
            s.replace(0, s.length, formatted)
        }
        Selection.setSelection(s, s.length)
        editing = false
    }

    private companion object {
        const val MAX_DIGITS = 8
    }
}

/**
 * Поле даты: ручной ввод с маской и календарь по нажатию на иконку (MaterialDatePicker).
 * Будущие даты в календаре недоступны.
 */
fun Fragment.setupDateField(layout: TextInputLayout, input: EditText) {
    input.addTextChangedListener(DateMaskWatcher())
    layout.setStartIconOnClickListener {
        val current = Validators.parseDate(input.text.toString())
        val builder = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.date_picker_title)
            .setCalendarConstraints(
                CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build()
            )
        current?.let { builder.setSelection(it.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()) }
        val picker = builder.build()
        // selectedDateUtcMillis — выбранная дата в миллисекундах UTC
        picker.addOnPositiveButtonClickListener { selectedDateUtcMillis ->
            val date = Instant.ofEpochMilli(selectedDateUtcMillis).atZone(ZoneOffset.UTC).toLocalDate()
            input.setText(Validators.formatDate(date))
        }
        picker.show(parentFragmentManager, "date_picker")
    }
}

@Suppress("unused")
fun LocalDate.toInputText(): String = Validators.formatDate(this)
