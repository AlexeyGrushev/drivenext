package ru.mtuci.drivenext.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.snackbar.Snackbar
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentSettingsBinding
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.main.MainNavigator

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentSettingsBinding.bind(view)
        val main = parentFragment as? MainNavigator

        binding.profileRow.setOnClickListener { main?.openProfile() }
        binding.bookingsRow.setOnClickListener { main?.openStub(R.string.stub_bookings) }
        binding.connectCarRow.setOnClickListener { main?.openStub(R.string.stub_add_car) }
        // Тема, уведомления, помощь и приглашение — разделы следующих версий
        listOf(binding.themeRow, binding.notificationsRow, binding.helpRow, binding.inviteRow).forEach { row ->
            row.setOnClickListener { Snackbar.make(binding.root, R.string.coming_soon, Snackbar.LENGTH_SHORT).show() }
        }

        launchOnStarted {
            viewModel.profile.collect { profile ->
                if (profile == null) return@collect
                binding.userName.text = profile.fullName
                binding.userEmail.text = profile.email
                binding.avatarImage.load(profile.avatarUrl) {
                    placeholder(R.drawable.ic_user_circle)
                    error(R.drawable.ic_user_circle)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load() // вернулись с экрана профиля — данные могли измениться
    }
}
