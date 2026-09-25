package ru.mtuci.drivenext.presentation.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.presentation.Navigator

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.destination.filterNotNull().collect {
                    (requireActivity() as Navigator).navigate(it)
                }
            }
        }
    }
}
