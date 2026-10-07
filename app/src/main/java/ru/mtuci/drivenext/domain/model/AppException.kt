package ru.mtuci.drivenext.domain.model

/** Ошибка с текстом, который можно показать пользователю в диалоге. */
class AppException(message: String, cause: Throwable? = null) : Exception(message, cause)
