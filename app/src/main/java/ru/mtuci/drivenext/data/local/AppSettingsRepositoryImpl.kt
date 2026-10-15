package ru.mtuci.drivenext.data.local

import android.content.Context
import androidx.core.content.edit
import ru.mtuci.drivenext.domain.model.ThemeMode
import ru.mtuci.drivenext.domain.repository.AppSettingsRepository

class AppSettingsRepositoryImpl(context: Context) : AppSettingsRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getThemeMode(): ThemeMode =
        runCatching { ThemeMode.valueOf(prefs.getString(KEY_THEME, null).orEmpty()) }
            .getOrDefault(ThemeMode.SYSTEM)

    override fun setThemeMode(mode: ThemeMode) {
        prefs.edit { putString(KEY_THEME, mode.name) }
    }

    override var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS, false)
        set(value) = prefs.edit { putBoolean(KEY_NOTIFICATIONS, value) }

    override var bookingNotifications: Boolean
        get() = prefs.getBoolean(KEY_BOOKING_NOTIFICATIONS, true)
        set(value) = prefs.edit { putBoolean(KEY_BOOKING_NOTIFICATIONS, value) }

    override var promoNotifications: Boolean
        get() = prefs.getBoolean(KEY_PROMO_NOTIFICATIONS, true)
        set(value) = prefs.edit { putBoolean(KEY_PROMO_NOTIFICATIONS, value) }

    private companion object {
        const val PREFS_NAME = "drivenext_settings"
        const val KEY_THEME = "theme_mode"
        const val KEY_NOTIFICATIONS = "notifications_enabled"
        const val KEY_BOOKING_NOTIFICATIONS = "notifications_bookings"
        const val KEY_PROMO_NOTIFICATIONS = "notifications_promo"
    }
}
