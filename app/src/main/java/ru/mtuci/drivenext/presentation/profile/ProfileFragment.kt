package ru.mtuci.drivenext.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.snackbar.Snackbar
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentProfileBinding
import ru.mtuci.drivenext.domain.model.Gender
import ru.mtuci.drivenext.presentation.common.PhotoPicker
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.common.navigator
import ru.mtuci.drivenext.presentation.common.showErrorDialog
import ru.mtuci.drivenext.presentation.main.MainNavigator

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels { appViewModelFactory() }
    private val photoPicker = PhotoPicker(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentProfileBinding.bind(view)

        binding.backButton.setOnClickListener { (parentFragment as? MainNavigator)?.popBack() }
        // Нажатие на аватар — загрузка нового (галерея или камера)
        binding.avatarImage.setOnClickListener { photoPicker.pick { uri -> viewModel.onAvatarPicked(uri.toString()) } }
        binding.avatarEditBadge.setOnClickListener { binding.avatarImage.performClick() }
        binding.passwordRow.setOnClickListener {
            Snackbar.make(binding.root, R.string.coming_soon, Snackbar.LENGTH_SHORT).show()
        }
        binding.logoutRow.setOnClickListener { viewModel.onLogoutClick() }

        launchOnStarted {
            viewModel.state.collect { state ->
                state.profile?.let { profile ->
                    binding.userName.text = profile.fullName
                    binding.emailValue.text = profile.email
                    binding.genderValue.text = when (profile.gender) {
                        Gender.MALE -> getString(R.string.gender_male)
                        Gender.FEMALE -> getString(R.string.gender_female)
                        null -> getString(R.string.gender_not_set)
                    }
                    binding.googleValue.text = profile.googleEmail ?: getString(R.string.google_not_linked)
                    binding.joinedText.isVisible = profile.joinedAt != null
                    profile.joinedAt?.let { date ->
                        val month = resources.getStringArray(R.array.months_prepositional)[date.monthValue - 1]
                        binding.joinedText.text = getString(R.string.profile_joined_format, month, date.year)
                    }
                    binding.avatarImage.load(profile.avatarUrl) {
                        placeholder(R.drawable.ic_user_circle)
                        error(R.drawable.ic_user_circle)
                    }
                }
                binding.avatarProgress.isVisible = state.isAvatarLoading
                binding.logoutText.setText(if (state.isLoggingOut) R.string.logout_loading else R.string.profile_logout)
                state.errorMessage?.let { message ->
                    showErrorDialog(message)
                    viewModel.onErrorShown()
                }
                if (state.loggedOut) navigator.openLoginAsRoot()
            }
        }
    }
}
