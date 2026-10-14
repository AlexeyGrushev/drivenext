package ru.mtuci.drivenext.presentation.details

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentCarDetailsBinding
import ru.mtuci.drivenext.presentation.Navigator
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.common.showErrorDialog
import ru.mtuci.drivenext.presentation.main.MainNavigator

/** Экран «Детали»: фото, название, адрес, описание, избранное и кнопка «Забронировать». */
class CarDetailsFragment : Fragment(R.layout.fragment_car_details) {

    private val viewModel: CarDetailsViewModel by viewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentCarDetailsBinding.bind(view)
        viewModel.start(requireArguments().getLong(ARG_CAR_ID))

        binding.backButton.setOnClickListener { (parentFragment as? MainNavigator)?.popBack() }
        binding.favoriteButton.setOnClickListener { viewModel.onFavoriteClick() }
        binding.states.retryButton.setOnClickListener { viewModel.load() }
        binding.bookButton.setOnClickListener {
            viewModel.state.value.car?.let { (requireActivity() as Navigator).openCheckout(it.id) }
        }

        launchOnStarted {
            viewModel.state.collect { state ->
                binding.progress.isVisible = state.isLoading
                binding.states.errorContainer.isVisible = state.loadFailed && !state.isLoading
                binding.states.errorText.setText(R.string.error_load_failed)
                binding.favoriteButton.isSelected = state.isFavorite

                val car = state.car
                val showContent = car != null && !state.loadFailed
                binding.content.isInvisible = !showContent
                binding.bottomBar.isInvisible = !showContent
                binding.favoriteButton.isVisible = showContent
                if (car != null) {
                    binding.detailsTitle.text = car.title
                    binding.addressText.text = car.address ?: getString(R.string.details_no_address)
                    binding.descriptionText.text = car.description ?: getString(R.string.details_no_description)
                    binding.priceText.text = getString(R.string.details_price_per_day_format, car.pricePerDay)
                    binding.detailsImage.load(car.mainImage) {
                        placeholder(R.drawable.car_placeholder)
                        error(R.drawable.car_placeholder)
                    }
                }
                state.favoriteError?.let { message ->
                    showErrorDialog(message)
                    viewModel.onFavoriteErrorShown()
                }
            }
        }
    }

    companion object {
        private const val ARG_CAR_ID = "car_id"

        fun newInstance(carId: Long) = CarDetailsFragment().apply {
            arguments = bundleOf(ARG_CAR_ID to carId)
        }
    }
}
