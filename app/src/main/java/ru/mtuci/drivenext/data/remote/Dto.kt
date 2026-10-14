package ru.mtuci.drivenext.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CarDto(
    val id: Long,
    val brand: String,
    val model: String,
    @SerialName("price_per_day") val pricePerDay: Int,
    val transmission: String,
    val fuel: String,
    @SerialName("image_url") val imageUrl: String? = null,
    val address: String? = null,
    val description: String? = null,
    val year: Int? = null,
    val mileage: Int? = null,
    val photos: List<String> = emptyList(),
)

/** Новый автомобиль владельца (ЛР5). */
@Serializable
data class CarInsertDto(
    val brand: String,
    val model: String,
    @SerialName("price_per_day") val pricePerDay: Int,
    val transmission: String,
    val fuel: String,
    val address: String,
    val description: String,
    val year: Int,
    val mileage: Int,
    @SerialName("owner_id") val ownerId: String,
    val status: String = "approved",
)

/** Краткие данные автомобиля внутри ответа по броням (встроенный select cars(...)). */
@Serializable
data class CarShortDto(
    val brand: String,
    val model: String,
    @SerialName("image_url") val imageUrl: String? = null,
    val photos: List<String> = emptyList(),
)

@Serializable
data class BookingDto(
    val id: Long,
    @SerialName("car_id") val carId: Long,
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String,
    val days: Int,
    @SerialName("price_per_day") val pricePerDay: Int,
    @SerialName("insurance_per_day") val insurancePerDay: Int,
    @SerialName("rental_total") val rentalTotal: Int,
    @SerialName("insurance_total") val insuranceTotal: Int,
    val total: Int,
    val deposit: Int,
    val address: String? = null,
    val status: String,
    @SerialName("cancelled_at") val cancelledAt: String? = null,
    val cars: CarShortDto? = null,
)

@Serializable
data class BookingInsertDto(
    @SerialName("car_id") val carId: Long,
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String,
    val days: Int,
    @SerialName("price_per_day") val pricePerDay: Int,
    @SerialName("insurance_per_day") val insurancePerDay: Int,
    @SerialName("rental_total") val rentalTotal: Int,
    @SerialName("insurance_total") val insuranceTotal: Int,
    val total: Int,
    val deposit: Int,
    val address: String?,
)

/** Строка таблицы profiles при чтении. */
@Serializable
data class ProfileDto(
    val id: String,
    val email: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("middle_name") val middleName: String? = null,
    @SerialName("birth_date") val birthDate: String? = null,
    val gender: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("license_number") val licenseNumber: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

/** Строка таблицы profiles при вставке (created_at ставит сама база). */
@Serializable
data class ProfileInsertDto(
    val id: String,
    val email: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("middle_name") val middleName: String?,
    @SerialName("birth_date") val birthDate: String,
    val gender: String,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("license_number") val licenseNumber: String,
    @SerialName("license_issue_date") val licenseIssueDate: String,
    @SerialName("license_photo_path") val licensePhotoPath: String,
    @SerialName("passport_photo_path") val passportPhotoPath: String,
)
