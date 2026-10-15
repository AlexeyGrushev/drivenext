package ru.mtuci.drivenext.presentation.notifications

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ru.mtuci.drivenext.domain.repository.AppSettingsRepository
import ru.mtuci.drivenext.domain.repository.Notifier

data class NotificationsUiState(
    /** Включены ли уведомления: переключатель включён и системное разрешение выдано. */
    val enabled: Boolean = false,
    val bookings: Boolean = true,
    val promo: Boolean = true,
)

class NotificationsViewModel(
    private val settings: AppSettingsRepository,
    private val notifier: Notifier,
) : ViewModel() {

    private val _state = MutableStateFlow(read())
    val state: StateFlow<NotificationsUiState> = _state

    /** Перечитывает настройки: пользователь мог отозвать разрешение в системных настройках. */
    fun refresh() {
        if (settings.notificationsEnabled && !notifier.hasPermission()) settings.notificationsEnabled = false
        _state.value = read()
    }

    fun hasPermission() = notifier.hasPermission()

    fun setEnabled(enabled: Boolean) {
        settings.notificationsEnabled = enabled
        _state.update { it.copy(enabled = enabled) }
    }

    fun setBookings(enabled: Boolean) {
        settings.bookingNotifications = enabled
        _state.update { it.copy(bookings = enabled) }
    }

    fun setPromo(enabled: Boolean) {
        settings.promoNotifications = enabled
        _state.update { it.copy(promo = enabled) }
    }

    fun sendTest() = notifier.sendTest()

    private fun read() = NotificationsUiState(
        enabled = settings.notificationsEnabled && notifier.hasPermission(),
        bookings = settings.bookingNotifications,
        promo = settings.promoNotifications,
    )
}
