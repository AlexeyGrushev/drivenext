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
