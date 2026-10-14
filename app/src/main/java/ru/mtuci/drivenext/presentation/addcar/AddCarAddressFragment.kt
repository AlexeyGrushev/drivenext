package ru.mtuci.drivenext.presentation.addcar

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentAddCarAddressBinding
import ru.mtuci.drivenext.presentation.common.navigator

/** Добавление автомобиля, шаг 1: адрес. «Далее» активна, пока адрес пустой — нет. */
class AddCarAddressFragment : Fragment(R.layout.fragment_add_car_address) {

    private val viewModel: AddCarViewModel by activityViewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentAddCarAddressBinding.bind(view)
        binding.addressInput.setText(viewModel.address)

        binding.backButton.setOnClickListener { navigator.back() }

        fun updateButton() {
            binding.nextButton.isEnabled = !binding.addressInput.text.isNullOrBlank()
        }
        binding.addressInput.doAfterTextChanged { updateButton() }
        updateButton()

        binding.nextButton.setOnClickListener {
            viewModel.address = binding.addressInput.text.toString().trim()
            navigator.openAddCarInfo()
        }
    }
}
