package ru.mtuci.drivenext.domain.repository

import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.model.NewCar

interface CarRepository {
    suspend fun getCars(): Result<List<Car>>

    /** Поиск по марке или модели (без учёта регистра). */
    suspend fun searchCars(query: String): Result<List<Car>>

    suspend fun getCar(id: Long): Result<Car>

    /** Создаёт автомобиль владельца (или обновляет уже созданный в этой сессии). Возвращает id. */
    suspend fun saveCar(car: NewCar, existingId: Long?): Result<Long>

    /** Загружает фото в хранилище и записывает ссылки в карточку автомобиля. */
    suspend fun uploadCarPhotos(carId: Long, imageUris: List<String>): Result<Unit>
}
