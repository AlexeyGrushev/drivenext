package ru.mtuci.drivenext.presentation.registration

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentSignUpStep2Binding
import ru.mtuci.drivenext.domain.model.Gender
import ru.mtuci.drivenext.presentation.common.navigator
import ru.mtuci.drivenext.presentation.common.setupDateField

class SignUpStep2Fragment : Fragment(R.layout.fragment_sign_up_step2) {

    private val viewModel: RegistrationViewModel by activityViewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentSignUpStep2Binding.bind(view)

        binding.lastNameInput.setText(viewModel.lastName)
        binding.firstNameInput.setText(viewModel.firstName)
        binding.middleNameInput.setText(viewModel.middleName)
        binding.birthDateInput.setText(viewModel.birthDate)
        when (viewModel.gender) {
            Gender.MALE -> binding.genderMale.isChecked = true
            Gender.FEMALE -> binding.genderFemale.isChecked = true
            null -> Unit
        }

        binding.header.backButton.setOnClickListener {
            saveInput(binding)
            navigator.back()
        }
        setupDateField(binding.birthDateLayout, binding.birthDateInput)

        // «Далее» активна, только когда заполнены обязательные поля (отчество необязательно)
        fun updateButton() {
            binding.nextButton.isEnabled = !binding.lastNameInput.text.isNullOrBlank() &&
                !binding.firstNameInput.text.isNullOrBlank() &&
                !binding.birthDateInput.text.isNullOrBlank() &&
                binding.genderGroup.checkedRadioButtonId != View.NO_ID
        }
        listOf(binding.lastNameInput, binding.firstNameInput, binding.middleNameInput, binding.birthDateInput)
            .forEach { it.doAfterTextChanged { updateButton() } }
        binding.genderGroup.setOnCheckedChangeListener { _, _ ->
            binding.genderError.isVisible = false
            updateButton()
        }
        updateButton()

        // Подсказка об обязательности: показываем, когда пользователь ушёл с пустого поля
        requireOnBlur(binding.lastNameInput, binding.lastNameLayout)
        requireOnBlur(binding.firstNameInput, binding.firstNameLayout)
        requireOnBlur(binding.birthDateInput, binding.birthDateLayout)
        binding.birthDateInput.doAfterTextChanged { binding.birthDateLayout.error = null }

        binding.nextButton.setOnClickListener {
            saveInput(binding)
            val errors = viewModel.validateStep2()
            binding.lastNameLayout.error = if (errors.lastNameEmpty) getString(R.string.error_fill_required) else null
            binding.firstNameLayout.error = if (errors.firstNameEmpty) getString(R.string.error_fill_required) else null
            binding.birthDateLayout.error =
                if (errors.birthDateInvalid) getString(R.string.error_birth_date_invalid) else null
            binding.genderError.isVisible = errors.genderMissing
            if (!errors.hasErrors) navigator.openSignUpStep3()
        }
    }

    private fun saveInput(binding: FragmentSignUpStep2Binding) {
        viewModel.lastName = binding.lastNameInput.text.toString().trim()
        viewModel.firstName = binding.firstNameInput.text.toString().trim()
        viewModel.middleName = binding.middleNameInput.text.toString().trim()
        viewModel.birthDate = binding.birthDateInput.text.toString().trim()
        viewModel.gender = when (binding.genderGroup.checkedRadioButtonId) {
            R.id.genderMale -> Gender.MALE
            R.id.genderFemale -> Gender.FEMALE
            else -> null
        }
    }

    private fun requireOnBlur(input: TextInputEditText, layout: TextInputLayout) {
        input.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && input.text.isNullOrBlank()) layout.error = getString(R.string.error_fill_required)
        }
        input.doAfterTextChanged { if (!it.isNullOrBlank()) layout.error = null }
    }
}
