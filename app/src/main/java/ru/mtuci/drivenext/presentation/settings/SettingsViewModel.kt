package ru.mtuci.drivenext.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.domain.model.ThemeMode
import ru.mtuci.drivenext.domain.model.UserProfile
import ru.mtuci.drivenext.domain.repository.AppSettingsRepository
import ru.mtuci.drivenext.domain.usecase.GetProfileUseCase

class SettingsViewModel(
    private val getProfile: GetProfileUseCase,
    private val appSettings: AppSettingsRepository,
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    val themeMode: ThemeMode get() = appSettings.getThemeMode()

    fun setThemeMode(mode: ThemeMode) = appSettings.setThemeMode(mode)

    /** Обновляет данные в шапке (имя, почта, аватар). Ошибка не критична — остаются прежние данные. */
    fun load() {
        viewModelScope.launch {
            getProfile().onSuccess { _profile.value = it }
        }
    }
}
