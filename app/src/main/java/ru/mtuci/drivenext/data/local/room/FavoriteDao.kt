package ru.mtuci.drivenext.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** DAO: описание запросов к таблице favorites. Реализацию генерирует Room при сборке. */
@Dao
interface FavoriteDao {

    // Flow сам присылает новый список при любом изменении таблицы
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    fun observeAll(userId: String): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND carId = :carId)")
    fun observeIsFavorite(userId: String, carId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND carId = :carId")
    suspend fun delete(userId: String, carId: Long)
}
