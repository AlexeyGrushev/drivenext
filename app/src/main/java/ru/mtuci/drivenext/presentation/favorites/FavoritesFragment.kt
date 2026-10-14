package ru.mtuci.drivenext.presentation.favorites

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentFavoritesBinding
import ru.mtuci.drivenext.presentation.cars.CarAdapter
import ru.mtuci.drivenext.presentation.cars.openBooking
import ru.mtuci.drivenext.presentation.cars.openCarDetails
import ru.mtuci.drivenext.presentation.common.launchOnStarted

/** «Избранное»: автомобили из локальной базы Room; список обновляется сам. */
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val viewModel: FavoritesViewModel by viewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentFavoritesBinding.bind(view)

        val adapter = CarAdapter(onBook = { openBooking(it) }, onDetails = { openCarDetails(it) })
        binding.favoritesList.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesList.adapter = adapter

        launchOnStarted {
            viewModel.favorites.collect { cars ->
                adapter.submitList(cars.orEmpty())
                binding.emptyText.isVisible = cars != null && cars.isEmpty()
            }
        }
    }
}
