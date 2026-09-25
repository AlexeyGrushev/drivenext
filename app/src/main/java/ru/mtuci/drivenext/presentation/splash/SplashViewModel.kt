package ru.mtuci.drivenext.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.domain.model.StartDestination
import ru.mtuci.drivenext.domain.usecase.ResolveStartDestinationUseCase

class SplashViewModel(
    private val resolveStartDestination: ResolveStartDestinationUseCase,
) : ViewModel() {

    private val _destination = MutableStateFlow<StartDestination?>(null)
    val destination: StateFlow<StartDestination?> = _destination

    init {
        viewModelScope.launch {
            // Инициализация идёт параллельно с минимальной задержкой экрана
            val resolved = async(Dispatchers.IO) { resolveStartDestination() }
            delay(SPLASH_DURATION_MS)
            _destination.value = resolved.await()
        }
    }

    private companion object {
        const val SPLASH_DURATION_MS = 2_500L // ТЗ: 2–3 секунды
    }
}
