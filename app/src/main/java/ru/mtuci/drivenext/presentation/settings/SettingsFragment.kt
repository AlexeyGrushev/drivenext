package ru.mtuci.drivenext.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentSettingsBinding
import ru.mtuci.drivenext.domain.model.ThemeMode
import ru.mtuci.drivenext.presentation.common.apply
import ru.mtuci.drivenext.presentation.common.copyToClipboard
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.Navigator
import ru.mtuci.drivenext.presentation.main.MainNavigator

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentSettingsBinding.bind(view)
        val main = parentFragment as? MainNavigator

        binding.profileRow.setOnClickListener { main?.openProfile() }
        binding.bookingsRow.setOnClickListener { (requireActivity() as Navigator).openBookings() }
        binding.connectCarRow.setOnClickListener { (requireActivity() as Navigator).openBecomeHost() }
        // Тема, уведомления, помощь и приглашение — разделы следующих версий
        binding.themeRow.setOnClickListener { showThemeDialog(binding) }
        binding.themeValue.setText(themeTitle(viewModel.themeMode))
        binding.helpRow.setOnClickListener { (requireActivity() as Navigator).openHelp() }
        binding.inviteRow.setOnClickListener {
            copyToClipboard(getString(R.string.invite_link))
            Snackbar.make(binding.root, R.string.invite_link_copied, Snackbar.LENGTH_SHORT).show()
        }
        listOf(binding.notificationsRow).forEach { row ->
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

    private fun themeTitle(mode: ThemeMode) = when (mode) {
        ThemeMode.SYSTEM -> R.string.theme_system
        ThemeMode.LIGHT -> R.string.theme_light
        ThemeMode.DARK -> R.string.theme_dark
    }

    /** Диалог выбора темы: выбранный вариант сохраняется и сразу применяется ко всему приложению. */
    private fun showThemeDialog(binding: FragmentSettingsBinding) {
        val modes = ThemeMode.entries
        val titles = modes.map { getString(themeTitle(it)) }.toTypedArray()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.settings_theme)
            .setSingleChoiceItems(titles, modes.indexOf(viewModel.themeMode)) { dialog, which ->
                val mode = modes[which]
                dialog.dismiss()
                if (mode != viewModel.themeMode) {
                    viewModel.setThemeMode(mode)
                    binding.themeValue.setText(themeTitle(mode))
                    mode.apply()
                }
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }
}
