package ru.mtuci.drivenext.data.repository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import java.time.Instant
import java.time.OffsetDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import ru.mtuci.drivenext.data.remote.BookingDto
import ru.mtuci.drivenext.data.remote.BookingInsertDto
import ru.mtuci.drivenext.data.remote.ProfileDto
import ru.mtuci.drivenext.data.remote.SupabaseProvider
import ru.mtuci.drivenext.data.remote.safeCall
import ru.mtuci.drivenext.domain.model.AppException
import ru.mtuci.drivenext.domain.model.Booking
import ru.mtuci.drivenext.domain.model.BookingStatus
import ru.mtuci.drivenext.domain.pricing.RentalQuote
import ru.mtuci.drivenext.domain.repository.BookingRepository

@Serializable
private data class BookingCancelDto(
    val status: String,
    @SerialName("cancelled_at") val cancelledAt: String,
)

@OptIn(ExperimentalTime::class)
class BookingRepositoryImpl(private val provider: SupabaseProvider) : BookingRepository {

    override suspend fun createBooking(
        carId: Long,
        address: String?,
        startAt: Instant,
        endAt: Instant,
        quote: RentalQuote,
    ): Result<Long> = safeCall("Не удалось оформить бронирование. Попробуйте снова.") {
        val dto = BookingInsertDto(
            carId = carId,
            startAt = startAt.toString(),
            endAt = endAt.toString(),
            days = quote.days,
            pricePerDay = quote.pricePerDay,
            insurancePerDay = quote.insurancePerDay,
            rentalTotal = quote.rentalTotal,
            insuranceTotal = quote.insuranceTotal,
            total = quote.total,
            deposit = quote.deposit,
            address = address,
        )
        provider.requireClient().from(TABLE).insert(dto) { select() }.decodeSingle<BookingDto>().id
    }

    override suspend fun getMyBookings(): Result<List<Booking>> =
        safeCall("Не удалось загрузить данные. Попробуйте снова.") {
            provider.requireClient().from(TABLE)
                .select(Columns.raw(SELECT_WITH_CAR)) { order("start_at", Order.DESCENDING) }
                .decodeList<BookingDto>()
                .map { it.toDomain() }
        }

    override suspend fun getBooking(id: Long): Result<Booking> =
        safeCall("Не удалось загрузить данные. Попробуйте снова.") {
            val client = provider.requireClient()
            val dto = client.from(TABLE)
                .select(Columns.raw(SELECT_WITH_CAR)) { filter { eq("id", id) } }
                .decodeSingle<BookingDto>()
            val userId = client.auth.currentUserOrNull()?.id
            val profile = userId?.let {
                client.from("profiles").select { filter { eq("id", it) } }.decodeSingleOrNull<ProfileDto>()
            }
            val driver = listOfNotNull(profile?.lastName, profile?.firstName, profile?.middleName)
                .filter { it.isNotBlank() }.joinToString(" ").ifBlank { null }
            dto.toDomain().copy(driverName = driver, licenseNumber = profile?.licenseNumber)
        }

    override suspend fun cancelBooking(id: Long): Result<Unit> =
        safeCall("Не удалось отменить бронирование. Попробуйте снова.") {
            val update = BookingCancelDto(status = "cancelled", cancelledAt = Instant.now().toString())
            provider.requireClient().from(TABLE).update(update) { filter { eq("id", id) } }
            Unit
        }

    private fun BookingDto.toDomain() = Booking(
        id = id,
        carId = carId,
        brand = cars?.brand.orEmpty(),
        model = cars?.model.orEmpty(),
        imageUrl = cars?.photos?.firstOrNull() ?: cars?.imageUrl,
        address = address,
        startAt = parse(startAt) ?: throw AppException("Некорректная дата бронирования."),
        endAt = parse(endAt) ?: throw AppException("Некорректная дата бронирования."),
        days = days,
        pricePerDay = pricePerDay,
        insurancePerDay = insurancePerDay,
        rentalTotal = rentalTotal,
        insuranceTotal = insuranceTotal,
        total = total,
        deposit = deposit,
        status = if (status == "cancelled") BookingStatus.CANCELLED else BookingStatus.ACTIVE,
        cancelledAt = cancelledAt?.let(::parse),
    )

    // Supabase отдаёт время как «2026-10-20T05:00:00+00:00»
    private fun parse(text: String): Instant? = runCatching { OffsetDateTime.parse(text).toInstant() }.getOrNull()

    private companion object {
        const val TABLE = "bookings"
        const val SELECT_WITH_CAR = "*, cars(brand,model,image_url,photos)"
    }
}
