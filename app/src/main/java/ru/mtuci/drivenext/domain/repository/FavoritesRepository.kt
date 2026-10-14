package ru.mtuci.drivenext.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.mtuci.drivenext.domain.model.Car

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<Car>>
    fun observeIsFavorite(carId: Long): Flow<Boolean>

    /** Добавляет или убирает автомобиль. Возвращает новое состояние (true — в избранном). */
    suspend fun toggle(car: Car): Result<Boolean>
}
