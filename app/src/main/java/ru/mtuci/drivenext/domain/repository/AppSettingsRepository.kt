package ru.mtuci.drivenext.domain.repository

import ru.mtuci.drivenext.domain.model.ThemeMode

/** Локальные настройки приложения (хранятся на устройстве). */
interface AppSettingsRepository {
    fun getThemeMode(): ThemeMode
    fun setThemeMode(mode: ThemeMode)

    /** Общий переключатель уведомлений приложения. */
    var notificationsEnabled: Boolean

    /** Уведомления о бронированиях (оформление, отмена). */
    var bookingNotifications: Boolean

    /** Акции и скидки. */
    var promoNotifications: Boolean
}
