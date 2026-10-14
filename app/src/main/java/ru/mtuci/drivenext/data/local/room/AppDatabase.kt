package ru.mtuci.drivenext.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/** Локальная база данных приложения (SQLite через Room). Один экземпляр на всё приложение. */
@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        // @Volatile: изменения INSTANCE сразу видны всем потокам
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            // synchronized: в блок одновременно заходит один поток, поэтому два потока
            // не создадут базу параллельно
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "drivenext_db",
                ).build().also { INSTANCE = it }
            }
    }
}
