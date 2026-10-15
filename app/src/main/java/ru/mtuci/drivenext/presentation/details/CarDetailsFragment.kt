package ru.mtuci.drivenext.presentation.details

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
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

        val photosAdapter = CarPhotosPagerAdapter()
        binding.photosPager.adapter = photosAdapter
        // Точки под фото: по одной на каждое фото, текущее выделено
        TabLayoutMediator(binding.photosDots, binding.photosPager) { _, _ -> }.attach()
        binding.photosPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val total = photosAdapter.itemCount
                binding.photosCounter.text = getString(R.string.details_photo_counter, position + 1, total)
            }
        })

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
                    val gallery = car.gallery.ifEmpty { listOf("") }
                    photosAdapter.submit(gallery)
                    val severalPhotos = car.gallery.size > 1
                    binding.photosDots.isVisible = severalPhotos
                    binding.photosCounter.isVisible = severalPhotos
                    binding.photosCounter.text =
                        getString(R.string.details_photo_counter, binding.photosPager.currentItem + 1, gallery.size)
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
