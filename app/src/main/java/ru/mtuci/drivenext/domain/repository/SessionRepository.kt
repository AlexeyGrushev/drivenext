package ru.mtuci.drivenext.domain.repository

interface SessionRepository {
    /** true, если есть действительный access token. Реальная проверка через Supabase — в следующих ЛР. */
    fun hasValidSession(): Boolean
}
