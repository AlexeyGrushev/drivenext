package ru.mtuci.drivenext.domain.repository

import ru.mtuci.drivenext.domain.model.ThemeMode

/** Локальные настройки приложения (хранятся на устройстве). */
interface AppSettingsRepository {
    fun getThemeMode(): ThemeMode
    fun setThemeMode(mode: ThemeMode)
}
