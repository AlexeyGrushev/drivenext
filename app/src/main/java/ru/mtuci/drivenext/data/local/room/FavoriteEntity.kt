package ru.mtuci.drivenext.data.local.room

import androidx.room.Entity

/**
 * Строка таблицы favorites: избранный автомобиль конкретного пользователя.
 * Храним карточку целиком, чтобы список избранного открывался без сети.
 * Составной ключ (userId + carId): у каждого пользователя свой список.
 */
@Entity(tableName = "favorites", primaryKeys = ["userId", "carId"])
data class FavoriteEntity(
    val userId: String,
    val carId: Long,
    val brand: String,
    val model: String,
    val pricePerDay: Int,
    val transmission: String,
    val fuel: String,
    val imageUrl: String?,
    val addedAt: Long,
)
