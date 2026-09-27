package ru.mtuci.drivenext.domain.repository

interface OnboardingRepository {
    fun isCompleted(): Boolean
    fun setCompleted()
}
