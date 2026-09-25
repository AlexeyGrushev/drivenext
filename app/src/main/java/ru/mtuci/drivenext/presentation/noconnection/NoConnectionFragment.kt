package ru.mtuci.drivenext.presentation.noconnection

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentNoConnectionBinding
import ru.mtuci.drivenext.presentation.Navigator

class NoConnectionFragment : Fragment(R.layout.fragment_no_connection) {

    private val viewModel: NoConnectionViewModel by viewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentNoConnectionBinding.bind(view)

        binding.retryButton.setOnClickListener { viewModel.retry() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progress.isVisible = state.isLoading
                    binding.retryButton.isEnabled = !state.isLoading
                    binding.retryButton.text =
                        if (state.isLoading) "" else getString(R.string.no_connection_retry)
                    state.destination?.let { (requireActivity() as Navigator).navigate(it) }
                }
            }
        }
    }
}
