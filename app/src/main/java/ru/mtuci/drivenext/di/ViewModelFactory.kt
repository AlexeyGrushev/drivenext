package ru.mtuci.drivenext.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.mtuci.drivenext.presentation.noconnection.NoConnectionViewModel
import ru.mtuci.drivenext.presentation.onboarding.OnboardingViewModel
import ru.mtuci.drivenext.presentation.splash.SplashViewModel

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        SplashViewModel::class.java -> SplashViewModel(container.resolveStartDestination)
        NoConnectionViewModel::class.java -> NoConnectionViewModel(container.resolveStartDestination)
        OnboardingViewModel::class.java -> OnboardingViewModel(container.onboardingRepository)
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    } as T
}
