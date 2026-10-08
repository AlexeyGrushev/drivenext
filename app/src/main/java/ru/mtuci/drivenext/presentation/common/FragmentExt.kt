package ru.mtuci.drivenext.presentation.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.presentation.Navigator

val Fragment.navigator: Navigator get() = requireActivity() as Navigator

/** Диалоговое окно с текстом ошибки (требование ТЗ: ошибки сервера показываем в диалоге). */
fun Fragment.showErrorDialog(message: String, onDismiss: () -> Unit = {}) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.dialog_error_title)
        .setMessage(message)
        .setPositiveButton(R.string.action_ok, null)
        .setOnDismissListener { onDismiss() }
        .show()
}

/** Запускает подписку на потоки, которая работает только пока экран виден. */
fun Fragment.launchOnStarted(block: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }
}
