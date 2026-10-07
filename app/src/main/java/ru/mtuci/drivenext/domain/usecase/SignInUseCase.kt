package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.repository.AuthRepository

class SignInUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        authRepository.signIn(email.trim(), password)
}
