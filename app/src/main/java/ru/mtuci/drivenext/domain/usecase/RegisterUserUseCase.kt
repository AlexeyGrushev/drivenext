package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.model.RegistrationData
import ru.mtuci.drivenext.domain.repository.AuthRepository
import ru.mtuci.drivenext.domain.repository.ProfileRepository

/** Регистрация: создаём аккаунт, затем сохраняем профиль и загружаем фото. */
class RegisterUserUseCase(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(data: RegistrationData): Result<Unit> =
        authRepository.signUp(data.email.trim(), data.password)
            .mapCatching { userId -> profileRepository.saveProfile(userId, data).getOrThrow() }
}
