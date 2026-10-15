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

    private companion object {
        const val PREFS_NAME = "drivenext_settings"
        const val KEY_THEME = "theme_mode"
    }
}
