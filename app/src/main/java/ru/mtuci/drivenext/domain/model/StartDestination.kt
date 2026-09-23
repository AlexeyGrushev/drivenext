package ru.mtuci.drivenext.domain.model

sealed interface StartDestination {
    data object NoConnection : StartDestination
    data object Onboarding : StartDestination
    data object Login : StartDestination
    data object Home : StartDestination
}
