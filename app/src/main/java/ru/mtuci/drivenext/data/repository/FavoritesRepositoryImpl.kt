package ru.mtuci.drivenext.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.mtuci.drivenext.data.local.room.FavoriteDao
import ru.mtuci.drivenext.data.local.room.FavoriteEntity
import ru.mtuci.drivenext.domain.model.AppException
import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.repository.AuthRepository
import ru.mtuci.drivenext.domain.repository.FavoritesRepository
import kotlin.coroutines.cancellation.CancellationException

/** Избранное хранится локально в Room и привязано к id пользователя. */
class FavoritesRepositoryImpl(
    private val dao: FavoriteDao,
    private val authRepository: AuthRepository,
) : FavoritesRepository {

    override fun observeFavorites(): Flow<List<Car>> {
        val userId = authRepository.currentUserId() ?: return flowOf(emptyList())
        return dao.observeAll(userId).map { list -> list.map { it.toCar() } }
    }

    override fun observeIsFavorite(carId: Long): Flow<Boolean> {
        val userId = authRepository.currentUserId() ?: return flowOf(false)
        return dao.observeIsFavorite(userId, carId)
    }

    override suspend fun toggle(car: Car): Result<Boolean> = try {
        val userId = authRepository.currentUserId() ?: throw AppException("Сессия истекла. Войдите в аккаунт снова.")
        if (dao.isFavorite(userId, car.id)) {
            dao.delete(userId, car.id)
            Result.success(false)
        } else {
            dao.insert(car.toEntity(userId))
            Result.success(true)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(AppException("Не удалось добавить в избранное. Попробуйте снова.", e))
    }

    private fun FavoriteEntity.toCar() =
        Car(carId, brand, model, pricePerDay, transmission, fuel, imageUrl)

    private fun Car.toEntity(userId: String) = FavoriteEntity(
        userId = userId,
        carId = id,
        brand = brand,
        model = model,
        pricePerDay = pricePerDay,
        transmission = transmission,
        fuel = fuel,
        imageUrl = mainImage,
        addedAt = System.currentTimeMillis(),
    )
}
