package ru.mtuci.drivenext.presentation.registration

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentSignUpStep3Binding
import ru.mtuci.drivenext.presentation.common.PhotoPicker
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.common.navigator
import ru.mtuci.drivenext.presentation.common.setupDateField
import ru.mtuci.drivenext.presentation.common.showErrorDialog

class SignUpStep3Fragment : Fragment(R.layout.fragment_sign_up_step3) {

    private val viewModel: RegistrationViewModel by activityViewModels { appViewModelFactory() }
    private val photoPicker = PhotoPicker(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentSignUpStep3Binding.bind(view)

        binding.licenseNumberInput.setText(viewModel.licenseNumber)
        binding.issueDateInput.setText(viewModel.issueDate)
        viewModel.avatarUri?.let { showAvatar(binding, Uri.parse(it)) }
        viewModel.licensePhotoUri?.let { showUploaded(binding.licensePhotoImage, binding.licensePhotoText, Uri.parse(it)) }
        viewModel.passportPhotoUri?.let { showUploaded(binding.passportPhotoImage, binding.passportPhotoText, Uri.parse(it)) }

        binding.header.backButton.setOnClickListener {
            saveInput(binding)
            navigator.back()
        }
        setupDateField(binding.issueDateLayout, binding.issueDateInput)

        // Аватар — необязательный
        binding.avatarImage.setOnClickListener {
            photoPicker.pick { uri ->
                viewModel.avatarUri = uri.toString()
                showAvatar(binding, uri)
            }
        }
        // Фото удостоверения и паспорта — обязательные
        listOf(
            binding.licensePhotoImage to binding.licensePhotoText,
            binding.passportPhotoImage to binding.passportPhotoText,
        ).forEachIndexed { index, (image, text) ->
            val onClick = View.OnClickListener {
                photoPicker.pick { uri ->
                    if (index == 0) viewModel.licensePhotoUri = uri.toString() else viewModel.passportPhotoUri = uri.toString()
                    showUploaded(image, text, uri)
                    binding.photosError.isVisible = false
                    updateButton(binding)
                }
            }
            image.setOnClickListener(onClick)
            text.setOnClickListener(onClick)
        }

        binding.licenseNumberInput.doAfterTextChanged {
            binding.licenseNumberLayout.error = null
            updateButton(binding)
        }
        binding.issueDateInput.doAfterTextChanged {
            binding.issueDateLayout.error = null
            updateButton(binding)
        }
        updateButton(binding)

        binding.nextButton.setOnClickListener {
            saveInput(binding)
            val errors = viewModel.validateStep3()
            binding.licenseNumberLayout.error =
                if (errors.licenseNumberInvalid) getString(R.string.error_license_number_invalid) else null
            binding.issueDateLayout.error =
                if (errors.issueDateInvalid) getString(R.string.error_issue_date_invalid) else null
            binding.photosError.isVisible = errors.photosMissing
            if (!errors.hasErrors) viewModel.register()
        }

        launchOnStarted {
            viewModel.state.collect { state ->
                binding.nextButton.isLoading = state.isLoading
                state.errorMessage?.let { message ->
                    showErrorDialog(message)
                    viewModel.onErrorShown()
                }
                if (state.registered) navigator.openRegistrationSuccess()
            }
        }
    }

    private fun saveInput(binding: FragmentSignUpStep3Binding) {
        viewModel.licenseNumber = binding.licenseNumberInput.text.toString().trim()
        viewModel.issueDate = binding.issueDateInput.text.toString().trim()
    }

    /** «Далее» активна, когда заполнены номер, дата выдачи и загружены оба фото. */
    private fun updateButton(binding: FragmentSignUpStep3Binding) {
        binding.nextButton.isEnabled = !binding.licenseNumberInput.text.isNullOrBlank() &&
            !binding.issueDateInput.text.isNullOrBlank() &&
            viewModel.licensePhotoUri != null &&
            viewModel.passportPhotoUri != null
    }

    private fun showAvatar(binding: FragmentSignUpStep3Binding, uri: Uri) {
        binding.avatarImage.load(uri)
    }

    private fun showUploaded(image: ImageView, text: TextView, uri: Uri) {
        image.setPadding(0, 0, 0, 0)
        image.scaleType = ImageView.ScaleType.CENTER_CROP
        image.load(uri)
        text.setText(R.string.photo_uploaded)
    }
}
