package ru.mtuci.drivenext.domain.repository

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Unit>

    /** Регистрирует пользователя и возвращает его id. */
    suspend fun signUp(email: String, password: String): Result<String>

    /** Запускает вход через Google OAuth (откроется браузер). Результат придёт по deep link. */
    suspend fun signInWithGoogle(): Result<Unit>

    suspend fun signOut(): Result<Unit>

    /** true, если есть сохранённая действительная сессия (токен). */
    suspend fun hasValidSession(): Boolean
}
