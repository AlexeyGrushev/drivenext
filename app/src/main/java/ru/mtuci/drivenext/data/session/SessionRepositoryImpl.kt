package ru.mtuci.drivenext.data.session

import ru.mtuci.drivenext.domain.repository.SessionRepository

/** Заглушка: пока нет авторизации через Supabase, сессии нет. */
class SessionRepositoryImpl : SessionRepository {
    override fun hasValidSession(): Boolean = false
}
