package ru.mtuci.drivenext.domain.repository

import ru.mtuci.drivenext.domain.model.Car

interface CarRepository {
    suspend fun getCars(): Result<List<Car>>

    /** Поиск по марке или модели (без учёта регистра). */
    suspend fun searchCars(query: String): Result<List<Car>>
}
