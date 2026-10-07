package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.repository.AuthRepository

class SignOutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = authRepository.signOut()
}
