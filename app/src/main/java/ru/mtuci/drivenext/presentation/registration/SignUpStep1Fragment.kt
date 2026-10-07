package ru.mtuci.drivenext.presentation.registration

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentSignUpStep1Binding
import ru.mtuci.drivenext.presentation.common.navigator

class SignUpStep1Fragment : Fragment(R.layout.fragment_sign_up_step1) {

    private val viewModel: RegistrationViewModel by activityViewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentSignUpStep1Binding.bind(view)

        // Возвращаемся с шага 2 — поля остаются заполненными
        binding.emailInput.setText(viewModel.email)
        binding.passwordInput.setText(viewModel.password)
        binding.passwordRepeatInput.setText(viewModel.passwordRepeat)
        binding.agreeCheckbox.isChecked = viewModel.agreed

        binding.header.backButton.setOnClickListener { navigator.back() }

        // Ошибка убирается, как только пользователь начинает исправлять поле
        binding.emailInput.doAfterTextChanged { binding.emailLayout.error = null }
        binding.passwordInput.doAfterTextChanged { binding.passwordLayout.error = null }
        binding.passwordRepeatInput.doAfterTextChanged { binding.passwordRepeatLayout.error = null }
        binding.agreeCheckbox.setOnCheckedChangeListener { _, _ -> binding.agreeError.isVisible = false }

        binding.nextButton.setOnClickListener {
            viewModel.email = binding.emailInput.text.toString().trim()
            viewModel.password = binding.passwordInput.text.toString()
            viewModel.passwordRepeat = binding.passwordRepeatInput.text.toString()
            viewModel.agreed = binding.agreeCheckbox.isChecked

            val errors = viewModel.validateStep1()
            binding.emailLayout.error = if (errors.emailInvalid) getString(R.string.error_email_invalid) else null
            binding.passwordLayout.error = when {
                errors.passwordEmpty -> getString(R.string.error_password_required)
                errors.passwordShort -> getString(R.string.error_password_short)
                else -> null
            }
            binding.passwordRepeatLayout.error =
                if (errors.passwordsMismatch) getString(R.string.error_passwords_mismatch) else null
            binding.agreeError.isVisible = errors.notAgreed

            if (!errors.hasErrors) navigator.openSignUpStep2()
        }
    }
}
