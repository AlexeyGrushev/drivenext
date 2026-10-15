package ru.mtuci.drivenext.presentation.notifications

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentNotificationsBinding
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.common.navigator

/** «Уведомления»: общий переключатель с запросом системного разрешения и выбор типов уведомлений. */
class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private val viewModel: NotificationsViewModel by viewModels { appViewModelFactory() }
    private var binding: FragmentNotificationsBinding? = null

    // Системный диалог разрешения (Android 13+); результат — выдано или нет
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        viewModel.setEnabled(granted)
        // Состояние во ViewModel могло не измениться, поэтому возвращаем переключатель вручную
        binding?.pushSwitch?.isChecked = granted
        if (!granted) showPermissionDenied()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentNotificationsBinding.bind(view)
        binding = b
        b.backButton.setOnClickListener { navigator.back() }

        // Слушатели срабатывают и при программной смене состояния, поэтому реагируем только на нажатие пользователя
        b.pushSwitch.setOnCheckedChangeListener { button, checked ->
            if (!button.isPressed) return@setOnCheckedChangeListener
            if (!checked) {
                viewModel.setEnabled(false)
            } else if (viewModel.hasPermission()) {
                viewModel.setEnabled(true)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        b.bookingsSwitch.setOnCheckedChangeListener { button, checked ->
            if (button.isPressed) viewModel.setBookings(checked)
        }
        b.promoSwitch.setOnCheckedChangeListener { button, checked ->
            if (button.isPressed) viewModel.setPromo(checked)
        }
        b.testButton.setOnClickListener { viewModel.sendTest() }

        launchOnStarted {
            viewModel.state.collect { state ->
                b.pushSwitch.isChecked = state.enabled
                b.bookingsSwitch.isChecked = state.bookings
                b.promoSwitch.isChecked = state.promo
                // Типы уведомлений доступны, только когда уведомления включены
                b.bookingsSwitch.isEnabled = state.enabled
                b.promoSwitch.isEnabled = state.enabled
                b.testButton.isEnabled = state.enabled
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh() // вернулись из системных настроек
    }

    /** Если пользователь отказал в разрешении — подсказываем, где его выдать вручную. */
    private fun showPermissionDenied() {
        val root = binding?.root ?: return
        Snackbar.make(root, R.string.notifications_permission_denied, Snackbar.LENGTH_LONG)
            .setAction(R.string.notifications_open_settings) {
                startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                )
            }
            .show()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
