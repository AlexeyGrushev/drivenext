package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.repository.CarRepository

class GetCarsUseCase(private val carRepository: CarRepository) {
    suspend operator fun invoke(): Result<List<Car>> = carRepository.getCars()
}
