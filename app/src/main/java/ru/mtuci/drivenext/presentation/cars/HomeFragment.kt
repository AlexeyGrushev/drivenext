package ru.mtuci.drivenext.presentation.cars

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentHomeBinding
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.main.MainNavigator

/** Главная: поиск и прокручиваемый список автомобилей, загруженный с сервера. */
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentHomeBinding.bind(view)

        val adapter = CarAdapter(onBook = { openBooking() }, onDetails = { openCarDetails() })
        binding.carsList.layoutManager = LinearLayoutManager(requireContext())
        binding.carsList.adapter = adapter

        binding.refresh.setColorSchemeResources(R.color.primary_dark)
        binding.refresh.setOnRefreshListener { viewModel.load() }
        binding.states.retryButton.setOnClickListener { viewModel.load() }

        // Подтверждение поиска (кнопка «поиск» на клавиатуре) → экран «Результаты поиска»
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val text = query?.trim().orEmpty()
                if (text.isNotEmpty()) {
                    binding.searchView.clearFocus()
                    (parentFragment as? MainNavigator)?.openSearchResults(text)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        launchOnStarted {
            viewModel.state.collect { state ->
                val showList = state.isLoaded
                binding.refresh.isRefreshing = state.isLoading && showList
                binding.progress.isVisible = state.isLoading && !showList
                binding.refresh.isVisible = showList || state.isLoading
                binding.states.errorContainer.isVisible = state.errorMessage != null && !state.isLoading
                binding.states.errorText.setText(R.string.error_load_failed)
                binding.states.emptyText.isVisible = showList && state.cars.isEmpty() && !state.isLoading
                binding.states.emptyText.setText(R.string.cars_empty)
                adapter.submitList(state.cars)
            }
        }
    }
}
