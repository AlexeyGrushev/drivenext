package ru.mtuci.drivenext.presentation.addcar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentCarPhotosBinding
import ru.mtuci.drivenext.presentation.common.MultiPhotoPicker
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.common.navigator
import ru.mtuci.drivenext.presentation.common.showErrorDialog

/** Добавление автомобиля: фотографии (до 5 за раз из галереи или серией с камеры). */
class CarPhotosFragment : Fragment(R.layout.fragment_car_photos) {

    private val viewModel: AddCarViewModel by activityViewModels { appViewModelFactory() }
    private val picker = MultiPhotoPicker(this, AddCarViewModel.MAX_PHOTOS)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentCarPhotosBinding.bind(view)

        val adapter = CarPhotosAdapter(
            onRemove = { viewModel.removePhoto(it) },
            onAdd = { addPhotos(binding) },
        )
        val layoutManager = GridLayoutManager(requireContext(), 2)
        // Главное фото и пустая плитка (если фото ещё нет) занимают всю ширину, остальные — по половине
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = when (val item = adapter.currentList.getOrNull(position)) {
                is PhotoItem.Photo -> if (item.isMain) 2 else 1
                PhotoItem.Add -> if (position == 0) 2 else 1
                null -> 1
            }
        }
        binding.photosList.layoutManager = layoutManager
        binding.photosList.adapter = adapter

        binding.backButton.setOnClickListener { navigator.back() }
        binding.nextButton.setOnClickListener { viewModel.submitPhotos() }

        launchOnStarted {
            viewModel.photos.collect { uris ->
                val items = uris.mapIndexed { index, uri -> PhotoItem.Photo(uri, isMain = index == 0) }
                adapter.submitList(if (uris.size < AddCarViewModel.MAX_PHOTOS) items + PhotoItem.Add else items)
                // «Далее» активна только после добавления фотографий
                binding.nextButton.isEnabled = uris.isNotEmpty()
            }
        }
        launchOnStarted {
            viewModel.isLoading.collect { binding.nextButton.isLoading = it }
        }
        launchOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    AddCarEvent.CarAdded -> navigator.openCarAdded()
                    is AddCarEvent.Error -> showErrorDialog(event.message)
                    AddCarEvent.InfoSaved -> Unit
                }
            }
        }
    }

    private fun addPhotos(binding: FragmentCarPhotosBinding) {
        val remaining = AddCarViewModel.MAX_PHOTOS - viewModel.photos.value.size
        if (remaining <= 0) {
            Snackbar.make(binding.root, getString(R.string.car_photos_limit, AddCarViewModel.MAX_PHOTOS), Snackbar.LENGTH_SHORT).show()
            return
        }
        picker.pick(remaining) { uris -> viewModel.addPhotos(uris.map { it.toString() }) }
    }
}
