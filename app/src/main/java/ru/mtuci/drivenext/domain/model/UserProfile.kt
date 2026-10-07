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
) {
    val fullName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ").ifBlank { email }
}
