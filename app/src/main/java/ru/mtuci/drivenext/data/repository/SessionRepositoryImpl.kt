package ru.mtuci.drivenext.data.repository

import ru.mtuci.drivenext.domain.repository.AuthRepository
import ru.mtuci.drivenext.domain.repository.SessionRepository

/** Есть ли сохранённая сессия: сам токен хранит и обновляет Supabase SDK. */
class SessionRepositoryImpl(private val authRepository: AuthRepository) : SessionRepository {
    override suspend fun hasValidSession(): Boolean = authRepository.hasValidSession()
}
