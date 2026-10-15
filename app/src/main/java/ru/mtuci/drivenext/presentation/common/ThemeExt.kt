package ru.mtuci.drivenext.presentation.common

import androidx.appcompat.app.AppCompatDelegate
import ru.mtuci.drivenext.domain.model.ThemeMode

/** Включает выбранную тему во всём приложении; открытые экраны пересоздаются сами. */
fun ThemeMode.apply() {
    AppCompatDelegate.setDefaultNightMode(
        when (this) {
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        }
    )
}
