package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.model.NewCar
import ru.mtuci.drivenext.domain.repository.CarRepository

class SaveCarUseCase(private val carRepository: CarRepository) {
    suspend operator fun invoke(car: NewCar, existingId: Long?): Result<Long> = carRepository.saveCar(car, existingId)
}
