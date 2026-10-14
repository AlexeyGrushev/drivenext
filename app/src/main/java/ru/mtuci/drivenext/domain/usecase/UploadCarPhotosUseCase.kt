package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.repository.CarRepository

class UploadCarPhotosUseCase(private val carRepository: CarRepository) {
    suspend operator fun invoke(carId: Long, imageUris: List<String>): Result<Unit> =
        carRepository.uploadCarPhotos(carId, imageUris)
}
