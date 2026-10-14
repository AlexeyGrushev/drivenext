package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.repository.FavoritesRepository

class ToggleFavoriteUseCase(private val favoritesRepository: FavoritesRepository) {
    suspend operator fun invoke(car: Car): Result<Boolean> = favoritesRepository.toggle(car)
}
