package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.repository.CarRepository

class SearchCarsUseCase(private val carRepository: CarRepository) {
    suspend operator fun invoke(query: String): Result<List<Car>> = carRepository.searchCars(query.trim())
}
