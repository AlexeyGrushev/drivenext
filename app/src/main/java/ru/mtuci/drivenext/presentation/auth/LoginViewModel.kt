package ru.mtuci.drivenext.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.domain.usecase.SignInUseCase
import ru.mtuci.drivenext.domain.usecase.SignInWithGoogleUseCase
import ru.mtuci.drivenext.domain.validation.Validators

data class LoginUiState(
    val isLoading: Boolean = false,
    val isGoogleLoading: Boolean = false,
    val emailInvalid: Boolean = false,
    val errorMessage: String? = null,
    val signedIn: Boolean = false,
)

class LoginViewModel(
    private val signIn: SignInUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    fun onEmailChanged() {
        if (_state.value.emailInvalid) _state.update { it.copy(emailInvalid = false) }
    }

    fun onLoginClick(email: String, password: String) {
        if (_state.value.isLoading) return
        if (!Validators.isEmailValid(email)) {
            _state.update { it.copy(emailInvalid = true) }
            return
        }
        _state.update { it.copy(isLoading = true, emailInvalid = false) }
        viewModelScope.launch {
            signIn(email, password)
                .onSuccess { _state.update { it.copy(isLoading = false, signedIn = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    fun onGoogleClick() {
        if (_state.value.isGoogleLoading) return
        _state.update { it.copy(isGoogleLoading = true) }
        viewModelScope.launch {
            // Успех означает только то, что браузер открыт; сессия придёт по deep link
            signInWithGoogle()
                .onSuccess { _state.update { it.copy(isGoogleLoading = false) } }
                .onFailure { e -> _state.update { it.copy(isGoogleLoading = false, errorMessage = e.message) } }
        }
    }

    fun onErrorShown() {
        _state.update { it.copy(errorMessage = null) }
    }
}
