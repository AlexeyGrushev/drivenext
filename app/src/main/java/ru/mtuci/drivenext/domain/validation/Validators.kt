package ru.mtuci.drivenext.domain.validation

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Locale

/** Чистые функции проверки полей. Не зависят от Android, поэтому легко тестируются. */
object Validators {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.(com|ru)$")

    /** Формат даты на экране: ДД/ММ/ГГГГ. STRICT — отсекает 31/02/2020 и подобное. */
    val DATE_FORMAT: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ROOT).withResolverStyle(ResolverStyle.STRICT)

    const val MIN_PASSWORD_LENGTH = 6
    const val LICENSE_NUMBER_LENGTH = 10

    fun isEmailValid(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    fun isPasswordValid(password: String): Boolean = password.length >= MIN_PASSWORD_LENGTH

    fun parseDate(text: String): LocalDate? =
        runCatching { LocalDate.parse(text.trim(), DATE_FORMAT) }.getOrNull()

    fun formatDate(date: LocalDate): String = date.format(DATE_FORMAT)

    /** Дата рождения: существует, не в будущем, не раньше 1900 года. */
    fun isBirthDateValid(text: String, today: LocalDate = LocalDate.now()): Boolean {
        val date = parseDate(text) ?: return false
        return !date.isAfter(today) && date.year >= 1900
    }

    /** Дата выдачи прав: существует, не в будущем. */
    fun isIssueDateValid(text: String, today: LocalDate = LocalDate.now()): Boolean {
        val date = parseDate(text) ?: return false
        return !date.isAfter(today) && date.year >= 1900
    }

    fun isLicenseNumberValid(number: String): Boolean =
        number.length == LICENSE_NUMBER_LENGTH && number.all { it.isDigit() }
}
