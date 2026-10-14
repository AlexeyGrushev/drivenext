package ru.mtuci.drivenext.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.repository.FavoritesRepository

class ObserveFavoritesUseCase(private val favoritesRepository: FavoritesRepository) {
    operator fun invoke(): Flow<List<Car>> = favoritesRepository.observeFavorites()
}
