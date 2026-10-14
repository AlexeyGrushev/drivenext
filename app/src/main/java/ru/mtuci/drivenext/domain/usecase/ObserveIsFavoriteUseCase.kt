package ru.mtuci.drivenext.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.mtuci.drivenext.domain.repository.FavoritesRepository

class ObserveIsFavoriteUseCase(private val favoritesRepository: FavoritesRepository) {
    operator fun invoke(carId: Long): Flow<Boolean> = favoritesRepository.observeIsFavorite(carId)
}
