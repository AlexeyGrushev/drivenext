package ru.mtuci.drivenext.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import ru.mtuci.drivenext.domain.model.AppException

/**
 * Единственная точка создания клиента Supabase.
 * URL и anon-ключ берутся из local.properties (см. app/build.gradle.kts).
 */
class SupabaseProvider(
    private val url: String,
    private val anonKey: String,
) {
    val isConfigured: Boolean get() = url.isNotBlank() && anonKey.isNotBlank()

    private val lazyClient: SupabaseClient by lazy {
        createSupabaseClient(supabaseUrl = url, supabaseKey = anonKey) {
            install(Auth) {
                // Адрес возврата после OAuth: ru.mtuci.drivenext://login-callback
                scheme = AUTH_SCHEME
                host = AUTH_HOST
            }
            install(Postgrest)
            install(Storage)
        }
    }

    /** Клиент Supabase; если ключи не заданы — понятная ошибка вместо падения. */
    fun requireClient(): SupabaseClient {
        if (!isConfigured) {
            throw AppException(
                "Сервер не настроен: добавьте supabase.url и supabase.anon.key в файл local.properties."
            )
        }
        return lazyClient
    }

    companion object {
        const val AUTH_SCHEME = "ru.mtuci.drivenext"
        const val AUTH_HOST = "login-callback"
    }
}
