package ru.mtuci.drivenext.data.repository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import ru.mtuci.drivenext.data.remote.SupabaseProvider
import ru.mtuci.drivenext.data.remote.safeCall
import ru.mtuci.drivenext.domain.model.AppException
import ru.mtuci.drivenext.domain.repository.AuthRepository

class AuthRepositoryImpl(private val provider: SupabaseProvider) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<Unit> =
        safeCall("Не удалось выполнить вход. Попробуйте снова.") {
            provider.requireClient().auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }

    override suspend fun signUp(email: String, password: String): Result<String> =
        safeCall("Не удалось зарегистрироваться. Попробуйте снова.") {
            val auth = provider.requireClient().auth
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            // Если подтверждение почты в Supabase выключено, сессия появляется сразу.
            // Если включено — пробуем войти; без подтверждения это не получится.
            if (auth.currentSessionOrNull() == null) {
                try {
                    auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }
                } catch (e: Exception) {
                    throw AppException(
                        "Аккаунт создан, но вход не выполнен. Подтвердите электронную почту и войдите.", e
                    )
                }
            }
            auth.currentUserOrNull()?.id
                ?: throw AppException("Не удалось получить данные пользователя после регистрации.")
        }

    override suspend fun signInWithGoogle(): Result<Unit> =
        safeCall("Не удалось выполнить вход через Google. Попробуйте снова.") {
            provider.requireClient().auth.signInWith(Google)
        }

    override suspend fun signOut(): Result<Unit> =
        safeCall("Не удалось выйти из аккаунта. Попробуйте снова.") {
            provider.requireClient().auth.signOut()
        }

    override suspend fun hasValidSession(): Boolean {
        if (!provider.isConfigured) return false
        val auth = provider.requireClient().auth
        auth.awaitInitialization()
        return auth.currentSessionOrNull() != null
    }
}
