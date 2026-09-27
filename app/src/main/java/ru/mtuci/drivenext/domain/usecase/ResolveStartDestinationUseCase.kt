package ru.mtuci.drivenext.domain.usecase

import ru.mtuci.drivenext.domain.model.StartDestination
import ru.mtuci.drivenext.domain.repository.NetworkChecker
import ru.mtuci.drivenext.domain.repository.OnboardingRepository
import ru.mtuci.drivenext.domain.repository.SessionRepository

class ResolveStartDestinationUseCase(
    private val networkChecker: NetworkChecker,
    private val onboardingRepository: OnboardingRepository,
    private val sessionRepository: SessionRepository,
) {
    operator fun invoke(): StartDestination = when {
        !networkChecker.isOnline() -> StartDestination.NoConnection
        !onboardingRepository.isCompleted() -> StartDestination.Onboarding
        sessionRepository.hasValidSession() -> StartDestination.Home
        else -> StartDestination.Login
    }
}
