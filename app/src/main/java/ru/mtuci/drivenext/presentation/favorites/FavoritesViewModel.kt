package ru.mtuci.drivenext.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.usecase.ObserveFavoritesUseCase

class FavoritesViewModel(observeFavorites: ObserveFavoritesUseCase) : ViewModel() {

    /** null — данные из Room ещё не пришли; пустой список — в избранном ничего нет. */
    val favorites: StateFlow<List<Car>?> = observeFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
