package ru.mtuci.drivenext.data.remote

import android.content.Intent
import io.github.jan.supabase.auth.handleDeeplinks

/** Принимает deep link после входа через Google (ru.mtuci.drivenext://login-callback) и сохраняет сессию. */
class OAuthCallbackHandler(private val provider: SupabaseProvider) {

    fun handle(intent: Intent?, onSuccess: () -> Unit) {
        if (intent == null || !provider.isConfigured) return
        provider.requireClient().handleDeeplinks(intent) { onSuccess() }
    }
}
