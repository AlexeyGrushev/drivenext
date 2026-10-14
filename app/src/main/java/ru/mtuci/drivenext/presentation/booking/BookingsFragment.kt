package ru.mtuci.drivenext.presentation.booking

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentBookingsBinding
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.common.navigator

/** «Мои бронирования»: активные, завершённые и отменённые аренды. */
class BookingsFragment : Fragment(R.layout.fragment_bookings) {

    private val viewModel: BookingsViewModel by viewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentBookingsBinding.bind(view)

        val adapter = BookingAdapter(onActiveClick = { navigator.openBookingDetails(it.id) })
        binding.bookingsList.layoutManager = LinearLayoutManager(requireContext())
        binding.bookingsList.adapter = adapter

        binding.backButton.setOnClickListener { navigator.back() }
        binding.refresh.setColorSchemeResources(R.color.primary_dark)
        binding.refresh.setOnRefreshListener { viewModel.load() }
        binding.states.retryButton.setOnClickListener { viewModel.load() }

        launchOnStarted {
            viewModel.state.collect { state ->
                binding.progress.isVisible = state.isLoading && !state.isLoaded
                binding.refresh.isRefreshing = state.isLoading && state.isLoaded
                binding.states.errorContainer.isVisible = state.loadFailed && !state.isLoading
                binding.states.errorText.setText(R.string.error_load_failed)
                binding.states.emptyText.isVisible = state.isLoaded && state.bookings.isEmpty() && !state.isLoading
                binding.states.emptyText.setText(R.string.bookings_empty)
                adapter.submitList(state.bookings)
            }
        }
    }

    // Возвращаемся с экрана деталей (возможно, бронь отменена) — обновляем список
    override fun onStart() {
        super.onStart()
        viewModel.load()
    }
}
