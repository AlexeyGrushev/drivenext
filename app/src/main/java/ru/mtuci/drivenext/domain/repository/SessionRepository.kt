package ru.mtuci.drivenext.domain.repository

interface SessionRepository {
    /** true, если есть действительный access token. */
    suspend fun hasValidSession(): Boolean
}
