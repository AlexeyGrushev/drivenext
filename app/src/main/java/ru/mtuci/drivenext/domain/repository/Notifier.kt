package ru.mtuci.drivenext.domain.repository

import java.time.LocalDate

/** Показ системных уведомлений. Если уведомления выключены или нет разрешения — ничего не делает. */
interface Notifier {
    fun bookingCreated(carTitle: String, start: LocalDate, end: LocalDate)
    fun bookingCancelled(carTitle: String)

    /** Проверочное уведомление из настроек. */
    fun sendTest()

    /** Выдано ли системное разрешение на уведомления (на Android 12 и ниже оно не требуется). */
    fun hasPermission(): Boolean
}
