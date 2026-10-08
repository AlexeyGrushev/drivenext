package ru.mtuci.drivenext.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.domain.model.UserProfile
import ru.mtuci.drivenext.domain.usecase.GetProfileUseCase
import ru.mtuci.drivenext.domain.usecase.SignOutUseCase
import ru.mtuci.drivenext.domain.usecase.UpdateAvatarUseCase

data class ProfileUiState(
    val profile: UserProfile? = null,
    val isAvatarLoading: Boolean = false,
    val isLoggingOut: Boolean = false,
    val loggedOut: Boolean = false,
    val errorMessage: String? = null,
)

class ProfileViewModel(
    private val getProfile: GetProfileUseCase,
    private val updateAvatar: UpdateAvatarUseCase,
    private val signOut: SignOutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state

    init {
        viewModelScope.launch {
            getProfile()
                .onSuccess { profile -> _state.update { it.copy(profile = profile) } }
                .onFailure { e -> _state.update { it.copy(errorMessage = e.message) } }
        }
    }

    fun onAvatarPicked(uri: String) {
        if (_state.value.isAvatarLoading) return
        _state.update { it.copy(isAvatarLoading = true) }
        viewModelScope.launch {
            updateAvatar(uri)
                .onSuccess { url ->
                    _state.update {
                        it.copy(isAvatarLoading = false, profile = it.profile?.copy(avatarUrl = url))
                    }
                }
                .onFailure { e -> _state.update { it.copy(isAvatarLoading = false, errorMessage = e.message) } }
        }
    }

    /** Выход: Supabase удаляет локальную сессию; дальше экран входа и очистка стека. */
    fun onLogoutClick() {
        if (_state.value.isLoggingOut) return
        _state.update { it.copy(isLoggingOut = true) }
        viewModelScope.launch {
            signOut()
                .onSuccess { _state.update { it.copy(isLoggingOut = false, loggedOut = true) } }
                .onFailure { e -> _state.update { it.copy(isLoggingOut = false, errorMessage = e.message) } }
        }
    }

    fun onErrorShown() {
        _state.update { it.copy(errorMessage = null) }
    }
}
