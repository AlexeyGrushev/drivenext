package ru.mtuci.drivenext.domain.model

import java.time.LocalDate

data class UserProfile(
    val id: String,
    val email: String,
    val lastName: String,
    val firstName: String,
    val middleName: String?,
    val birthDate: LocalDate?,
    val gender: Gender?,
    val avatarUrl: String?,
    val googleEmail: String?,
    val joinedAt: LocalDate?,
    val licenseNumber: String? = null,
) {
    /** ФИО целиком (фамилия, имя, отчество) для документов. */
    val fullNameWithPatronymic: String
        get() = listOfNotNull(lastName, firstName, middleName).filter { it.isNotBlank() }.joinToString(" ")

    val fullName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ").ifBlank { email }
}
