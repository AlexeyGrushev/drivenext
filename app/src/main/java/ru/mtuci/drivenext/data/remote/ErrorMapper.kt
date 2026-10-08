package ru.mtuci.drivenext.data.remote

import io.ktor.client.plugins.HttpRequestTimeoutException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import ru.mtuci.drivenext.domain.model.AppException

/** Превращает любое исключение в AppException с текстом для пользователя. */
fun Throwable.toAppException(default: String): AppException {
    if (this is AppException) return this
    if (this is CancellationException) throw this
    val text = message.orEmpty().lowercase()
    val userMessage = when {
        this is IOException || this is HttpRequestTimeoutException ->
            "Нет подключения к интернету. Проверьте сеть и повторите попытку."
        "invalid login credentials" in text || "invalid_credentials" in text ->
            "Неверная электронная почта или пароль."
        "already registered" in text || "user_already_exists" in text || "already been registered" in text ->
            "Пользователь с такой электронной почтой уже зарегистрирован."
        "email not confirmed" in text || "email_not_confirmed" in text ->
            "Электронная почта не подтверждена. Подтвердите её и повторите вход."
        "rate limit" in text || "over_email_send_rate_limit" in text ->
            "Слишком много попыток. Попробуйте позже."
        "password" in text && "characters" in text ->
            "Пароль слишком короткий. Используйте не менее 6 символов."
        else -> message?.takeIf { it.isNotBlank() } ?: default
    }
    return AppException(userMessage, this)
}

/** Оборачивает suspend-вызов в Result и приводит ошибки к AppException. */
suspend inline fun <T> safeCall(default: String, crossinline block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e.toAppException(default))
    }
