package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.repository.CarRepository

class GetCarUseCase(private val carRepository: CarRepository) {
    suspend operator fun invoke(id: Long): Result<Car> = carRepository.getCar(id)
}
