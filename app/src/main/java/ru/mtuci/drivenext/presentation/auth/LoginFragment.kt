package ru.mtuci.drivenext.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentLoginBinding
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.common.navigator
import ru.mtuci.drivenext.presentation.common.showErrorDialog

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentLoginBinding.bind(view)

        // «Войти» активна, только когда заполнены оба поля
        fun updateButton() {
            binding.loginButton.isEnabled =
                !binding.emailInput.text.isNullOrBlank() && !binding.passwordInput.text.isNullOrEmpty()
        }
        binding.emailInput.doAfterTextChanged {
            viewModel.onEmailChanged()
            updateButton()
        }
        binding.passwordInput.doAfterTextChanged { updateButton() }
        updateButton()

        binding.loginButton.setOnClickListener {
            viewModel.onLoginClick(binding.emailInput.text.toString(), binding.passwordInput.text.toString())
        }
        binding.googleButton.progressColorRes = R.color.primary_dark
        binding.googleButton.setOnClickListener { viewModel.onGoogleClick() }
        binding.registerButton.setOnClickListener { navigator.openSignUpStep1() }

        // Стрелка «Назад» нужна, только если есть предыдущий экран (пришли с «Вход/регистрация»).
        // После выхода из профиля вход — корневой экран, и возвращаться некуда.
        binding.backButton.isVisible = parentFragmentManager.backStackEntryCount > 0
        binding.backButton.setOnClickListener { navigator.back() }
        binding.forgotPassword.setOnClickListener {
            Snackbar.make(binding.root, R.string.coming_soon, Snackbar.LENGTH_SHORT).show()
        }

        launchOnStarted {
            viewModel.state.collect { state ->
                binding.loginButton.isLoading = state.isLoading
                binding.googleButton.isLoading = state.isGoogleLoading
                binding.emailLayout.error = if (state.emailInvalid) getString(R.string.error_email_invalid) else null
                state.errorMessage?.let { message ->
                    showErrorDialog(message) { viewModel.onErrorShown() }
                    viewModel.onErrorShown()
                }
                if (state.signedIn) navigator.openHome()
            }
        }
    }
}
