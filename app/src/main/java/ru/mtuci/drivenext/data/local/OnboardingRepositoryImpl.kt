package ru.mtuci.drivenext.data.local

import android.content.Context
import androidx.core.content.edit
import ru.mtuci.drivenext.domain.repository.OnboardingRepository

class OnboardingRepositoryImpl(context: Context) : OnboardingRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isCompleted(): Boolean = prefs.getBoolean(KEY_COMPLETED, false)

    override fun setCompleted() {
        prefs.edit { putBoolean(KEY_COMPLETED, true) }
    }

    private companion object {
        const val PREFS_NAME = "drivenext_prefs"
        const val KEY_COMPLETED = "onboarding_completed"
    }
}
