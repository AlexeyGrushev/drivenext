package ru.mtuci.drivenext.presentation.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.domain.model.Gender
import ru.mtuci.drivenext.domain.model.RegistrationData
import ru.mtuci.drivenext.domain.usecase.RegisterUserUseCase
import ru.mtuci.drivenext.domain.validation.Validators

data class Step1Errors(
    val emailInvalid: Boolean = false,
    val passwordEmpty: Boolean = false,
    val passwordShort: Boolean = false,
    val passwordsMismatch: Boolean = false,
    val notAgreed: Boolean = false,
) {
    val hasErrors get() = emailInvalid || passwordEmpty || passwordShort || passwordsMismatch || notAgreed
}

data class Step2Errors(
    val lastNameEmpty: Boolean = false,
    val firstNameEmpty: Boolean = false,
    val birthDateInvalid: Boolean = false,
    val genderMissing: Boolean = false,
) {
    val hasErrors get() = lastNameEmpty || firstNameEmpty || birthDateInvalid || genderMissing
}

data class Step3Errors(
    val licenseNumberInvalid: Boolean = false,
    val issueDateInvalid: Boolean = false,
    val photosMissing: Boolean = false,
) {
    val hasErrors get() = licenseNumberInvalid || issueDateInvalid || photosMissing
}

data class RegistrationUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registered: Boolean = false,
)

/**
 * Одна ViewModel на все три шага регистрации (общая для Activity):
 * данные сохраняются при переходах «Далее» и «Назад», поэтому поля остаются заполненными.
 */
class RegistrationViewModel(private val registerUser: RegisterUserUseCase) : ViewModel() {

    // Шаг 1
    var email = ""
    var password = ""
    var passwordRepeat = ""
    var agreed = false

    // Шаг 2
    var lastName = ""
    var firstName = ""
    var middleName = ""
    var birthDate = ""
    var gender: Gender? = null

    // Шаг 3
    var avatarUri: String? = null
    var licenseNumber = ""
    var issueDate = ""
    var licensePhotoUri: String? = null
    var passportPhotoUri: String? = null

    private val _state = MutableStateFlow(RegistrationUiState())
    val state: StateFlow<RegistrationUiState> = _state

    fun validateStep1(): Step1Errors = Step1Errors(
        emailInvalid = !Validators.isEmailValid(email),
        passwordEmpty = password.isEmpty(),
        passwordShort = password.isNotEmpty() && !Validators.isPasswordValid(password),
        passwordsMismatch = password != passwordRepeat,
        notAgreed = !agreed,
    )

    fun validateStep2(): Step2Errors = Step2Errors(
        lastNameEmpty = lastName.isBlank(),
        firstNameEmpty = firstName.isBlank(),
        birthDateInvalid = !Validators.isBirthDateValid(birthDate),
        genderMissing = gender == null,
    )

    fun validateStep3(): Step3Errors = Step3Errors(
        licenseNumberInvalid = !Validators.isLicenseNumberValid(licenseNumber),
        issueDateInvalid = !Validators.isIssueDateValid(issueDate),
        photosMissing = licensePhotoUri == null || passportPhotoUri == null,
    )

    /** Финальный шаг: создаём аккаунт, загружаем фото и сохраняем профиль. */
    fun register() {
        if (_state.value.isLoading) return
        val data = buildData() ?: return
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            registerUser(data)
                .onSuccess { _state.update { it.copy(isLoading = false, registered = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    fun onErrorShown() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun buildData(): RegistrationData? {
        val birth: LocalDate = Validators.parseDate(birthDate) ?: return null
        val issue: LocalDate = Validators.parseDate(issueDate) ?: return null
        return RegistrationData(
            email = email,
            password = password,
            lastName = lastName,
            firstName = firstName,
            middleName = middleName.ifBlank { null },
            birthDate = birth,
            gender = gender ?: return null,
            avatarUri = avatarUri,
            licenseNumber = licenseNumber,
            licenseIssueDate = issue,
            licensePhotoUri = licensePhotoUri ?: return null,
            passportPhotoUri = passportPhotoUri ?: return null,
        )
    }
}
