package ru.mtuci.drivenext.domain.repository

import ru.mtuci.drivenext.domain.model.RegistrationData
import ru.mtuci.drivenext.domain.model.UserProfile

interface ProfileRepository {
    /** Профиль текущего пользователя (если строки профиля нет — минимальный по данным аккаунта). */
    suspend fun getProfile(): Result<UserProfile>

    /** Загружает фото и сохраняет профиль нового пользователя. */
    suspend fun saveProfile(userId: String, data: RegistrationData): Result<Unit>

    /** Загружает новый аватар и возвращает его URL. */
    suspend fun updateAvatar(imageUri: String): Result<String>
}
