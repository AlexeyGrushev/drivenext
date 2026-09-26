package ru.mtuci.drivenext.presentation.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingPage(
    @param:DrawableRes val imageRes: Int,
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int,
    /** Доля ширины экрана, которую занимает иллюстрация (по макету). */
    val imageWidthPercent: Float = 1f,
)
