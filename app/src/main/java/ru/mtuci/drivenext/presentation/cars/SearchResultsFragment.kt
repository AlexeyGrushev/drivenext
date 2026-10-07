package ru.mtuci.drivenext.presentation.cars

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentSearchResultsBinding
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.main.MainNavigator

/** «Результаты поиска»: сначала экран загрузки (Loader), затем карточки из ответа сервера. */
class SearchResultsFragment : Fragment(R.layout.fragment_search_results) {

    private val viewModel: SearchResultsViewModel by viewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentSearchResultsBinding.bind(view)
        viewModel.start(requireArguments().getString(ARG_QUERY).orEmpty())

        binding.header.headerTitle.setText(R.string.search_results_title)
        binding.header.backButton.setOnClickListener { (parentFragment as? MainNavigator)?.popBack() }

        val adapter = CarAdapter(onBook = { openBooking() }, onDetails = { openCarDetails() })
        binding.carsList.layoutManager = LinearLayoutManager(requireContext())
        binding.carsList.adapter = adapter
        binding.states.retryButton.setOnClickListener { viewModel.retry() }

        launchOnStarted {
            viewModel.state.collect { state ->
                binding.loader.isVisible = state.isLoading
                binding.states.errorContainer.isVisible = state.errorMessage != null && !state.isLoading
                binding.states.errorText.setText(R.string.error_search_failed)
                binding.states.emptyText.isVisible = state.isLoaded && state.cars.isEmpty() && !state.isLoading
                binding.states.emptyText.setText(R.string.search_empty)
                adapter.submitList(state.cars)
            }
        }
    }

    companion object {
        private const val ARG_QUERY = "query"

        fun newInstance(query: String) = SearchResultsFragment().apply {
            arguments = bundleOf(ARG_QUERY to query)
        }
    }
}
