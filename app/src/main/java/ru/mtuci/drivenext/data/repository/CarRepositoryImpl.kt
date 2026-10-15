package ru.mtuci.drivenext.data.repository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlin.time.ExperimentalTime
import ru.mtuci.drivenext.data.remote.CarDto
import ru.mtuci.drivenext.data.remote.CarInsertDto
import ru.mtuci.drivenext.data.remote.ImageReader
import ru.mtuci.drivenext.data.remote.SupabaseProvider
import ru.mtuci.drivenext.data.remote.safeCall
import ru.mtuci.drivenext.domain.model.AppException
import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.domain.model.NewCar
import ru.mtuci.drivenext.domain.repository.CarRepository

@Serializable
private data class CarPhotosUpdateDto(
    val photos: List<String>,
    @SerialName("image_url") val imageUrl: String?,
)

@OptIn(ExperimentalTime::class)
class CarRepositoryImpl(
    private val provider: SupabaseProvider,
    private val imageReader: ImageReader,
) : CarRepository {

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

    override suspend fun getCar(id: Long): Result<Car> =
        safeCall("Не удалось загрузить данные. Попробуйте снова.") {
            provider.requireClient().from(TABLE)
                .select { filter { eq("id", id) } }
                .decodeSingle<CarDto>()
                .toDomain()
        }

    override suspend fun saveCar(car: NewCar, existingId: Long?): Result<Long> =
        safeCall("Не удалось отправить данные. Попробуйте снова.") {
            val client = provider.requireClient()
            val ownerId = client.auth.currentUserOrNull()?.id
                ?: throw AppException("Сессия истекла. Войдите в аккаунт снова.")
            val dto = CarInsertDto(
                brand = car.brand.trim(),
                model = car.model.trim(),
                pricePerDay = car.pricePerDay,
                transmission = car.transmission,
                fuel = DEFAULT_FUEL,
                address = car.address.trim(),
                description = car.description.trim(),
                year = car.year,
                mileage = car.mileage,
                ownerId = ownerId,
            )
            if (existingId == null) {
                client.from(TABLE).insert(dto) { select() }.decodeSingle<CarDto>().id
            } else {
                client.from(TABLE).update(dto) { filter { eq("id", existingId) } }
                existingId
            }
        }

    override suspend fun uploadCarPhotos(carId: Long, imageUris: List<String>): Result<Unit> =
        safeCall("Не удалось загрузить фотографии. Попробуйте снова.") {
            val client = provider.requireClient()
            val ownerId = client.auth.currentUserOrNull()?.id
                ?: throw AppException("Сессия истекла. Войдите в аккаунт снова.")
            val bucket = client.storage.from(BUCKET)
            val stamp = System.currentTimeMillis()
            val urls = imageUris.mapIndexed { index, uri ->
                val path = "$ownerId/car_${carId}_${stamp}_$index.jpg"
                bucket.upload(path, imageReader.readJpeg(uri)) { upsert = true }
                bucket.publicUrl(path)
            }
            client.from(TABLE)
                .update(CarPhotosUpdateDto(photos = urls, imageUrl = urls.firstOrNull())) {
                    filter { eq("id", carId) }
                }
            Unit
        }

    private fun CarDto.toDomain() =
        Car(id, brand, model, pricePerDay, transmission, fuel, imageUrl, address, description, year, mileage, photos)

    private companion object {
        const val TABLE = "cars"
        const val BUCKET = "cars"

        // В макете нет поля цены, поэтому новому автомобилю назначается цена по умолчанию
        const val DEFAULT_FUEL = "Бензин"
    }
}
