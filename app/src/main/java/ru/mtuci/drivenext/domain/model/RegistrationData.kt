package ru.mtuci.drivenext.domain.model

import java.time.LocalDate

/** Все данные, собранные на трёх шагах регистрации. Фото передаются как строка Uri. */
data class RegistrationData(
    val email: String,
    val password: String,
    val lastName: String,
    val firstName: String,
    val middleName: String?,
    val birthDate: LocalDate,
    val gender: Gender,
    val avatarUri: String?,
    val licenseNumber: String,
    val licenseIssueDate: LocalDate,
    val licensePhotoUri: String,
    val passportPhotoUri: String,
)
