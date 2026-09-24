package ru.mtuci.drivenext.di

import android.content.Context
import ru.mtuci.drivenext.data.local.OnboardingRepositoryImpl
import ru.mtuci.drivenext.data.network.NetworkCheckerImpl
import ru.mtuci.drivenext.data.session.SessionRepositoryImpl
import ru.mtuci.drivenext.domain.repository.NetworkChecker
import ru.mtuci.drivenext.domain.repository.OnboardingRepository
import ru.mtuci.drivenext.domain.repository.SessionRepository
import ru.mtuci.drivenext.domain.usecase.ResolveStartDestinationUseCase

/** Ручной DI-контейнер: создаёт зависимости один раз на всё приложение. */
class AppContainer(context: Context) {
    private val networkChecker: NetworkChecker = NetworkCheckerImpl(context)
    private val sessionRepository: SessionRepository = SessionRepositoryImpl()
    val onboardingRepository: OnboardingRepository = OnboardingRepositoryImpl(context)

    val resolveStartDestination = ResolveStartDestinationUseCase(
        networkChecker, onboardingRepository, sessionRepository
    )
}
