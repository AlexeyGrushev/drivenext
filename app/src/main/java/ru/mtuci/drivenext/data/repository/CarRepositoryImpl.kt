package ru.mtuci.drivenext.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import ru.mtuci.drivenext.data.remote.CarDto
import ru.mtuci.drivenext.data.remote.SupabaseProvider
import ru.mtuci.drivenext.data.remote.safeCall
import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.repository.CarRepository

class CarRepositoryImpl(private val provider: SupabaseProvider) : CarRepository {

    override suspend fun getCars(): Result<List<Car>> =
        safeCall("Не удалось загрузить данные. Попробуйте снова.") {
            provider.requireClient().from(TABLE)
                .select { order("id", Order.ASCENDING) }
                .decodeList<CarDto>()
                .map { it.toDomain() }
        }

    override suspend fun searchCars(query: String): Result<List<Car>> =
        safeCall("Не удалось выполнить поиск. Попробуйте снова.") {
            val pattern = "%${query.replace("%", "").replace("_", "")}%"
            provider.requireClient().from(TABLE)
                .select {
                    filter {
                        or {
                            ilike("brand", pattern)
                            ilike("model", pattern)
                        }
                    }
                    order("id", Order.ASCENDING)
                }
                .decodeList<CarDto>()
                .map { it.toDomain() }
        }

    private fun CarDto.toDomain() = Car(id, brand, model, pricePerDay, transmission, fuel, imageUrl)

    private companion object {
        const val TABLE = "cars"
    }
}
