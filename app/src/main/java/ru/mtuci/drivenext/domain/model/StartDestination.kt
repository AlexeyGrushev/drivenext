package ru.mtuci.drivenext.domain.model

sealed interface StartDestination {
    data object NoConnection : StartDestination
    data object Onboarding : StartDestination

    /** Экран выбора «Войти / Зарегистрироваться» (Getting started). */
    data object Welcome : StartDestination
    data object Home : StartDestination
}
