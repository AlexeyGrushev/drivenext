package ru.mtuci.drivenext.presentation.onboarding

import androidx.lifecycle.ViewModel
import ru.mtuci.drivenext.domain.repository.OnboardingRepository

class OnboardingViewModel(
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {
    fun complete() = onboardingRepository.setCompleted()
}
