package ru.mtuci.drivenext.presentation.addcar

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentAddCarInfoBinding
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.common.navigator
import ru.mtuci.drivenext.presentation.common.showErrorDialog

/** Добавление автомобиля, шаг 2: данные автомобиля, трансмиссия выбирается в Spinner. */
class AddCarInfoFragment : Fragment(R.layout.fragment_add_car_info) {

    private val viewModel: AddCarViewModel by activityViewModels { appViewModelFactory() }

    /** Значения, которые попадут в базу, в том же порядке, что и подписи в списке (без подсказки). */
    private val transmissionCodes = listOf("A/T", "M/T", "AMT", "CVT")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentAddCarInfoBinding.bind(view)

        binding.yearInput.setText(viewModel.year)
        binding.brandInput.setText(viewModel.brand)
        binding.modelInput.setText(viewModel.model)
        binding.mileageInput.setText(viewModel.mileage)
        binding.priceInput.setText(viewModel.price)
        binding.descriptionInput.setText(viewModel.description)

        binding.backButton.setOnClickListener {
            saveInput(binding)
            navigator.back()
        }

        // Spinner: адаптер связывает список строк с выпадающим меню; первый пункт — подсказка
        val labels = listOf(
            getString(R.string.add_car_transmission_hint),
            getString(R.string.transmission_at),
            getString(R.string.transmission_mt),
            getString(R.string.transmission_amt),
            getString(R.string.transmission_cvt),
        )
        binding.transmissionSpinner.adapter = HintSpinnerAdapter(requireContext(), labels)
        viewModel.transmission?.let { saved ->
            binding.transmissionSpinner.setSelection(transmissionCodes.indexOf(saved) + 1)
        }
        binding.transmissionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Пользователь выбрал элемент: позиция 0 — подсказка, значит выбора ещё нет
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                viewModel.transmission = transmissionCodes.getOrNull(position - 1)
                updateButton(binding)
            }

            // Состояние, когда выбор отсутствует
            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.transmission = null
                updateButton(binding)
            }
        }

        listOf(
            binding.yearInput, binding.brandInput, binding.modelInput,
            binding.mileageInput, binding.priceInput, binding.descriptionInput,
        ).forEach { field ->
            field.doAfterTextChanged {
                binding.yearLayout.error = null
                binding.mileageLayout.error = null
                binding.priceLayout.error = null
                saveInput(binding)
                updateButton(binding)
            }
        }
        updateButton(binding)

        binding.submitButton.setOnClickListener {
            saveInput(binding)
            val errors = viewModel.validateInfo()
            binding.yearLayout.error = if (errors.yearInvalid) getString(R.string.add_car_error_year) else null
            binding.mileageLayout.error = if (errors.mileageInvalid) getString(R.string.add_car_error_mileage) else null
            binding.priceLayout.error = if (errors.priceInvalid) getString(R.string.add_car_error_price) else null
            if (!errors.hasErrors) viewModel.submitInfo()
        }

        launchOnStarted {
            viewModel.isLoading.collect { binding.submitButton.isLoading = it }
        }
        launchOnStarted {
            viewModel.events.collect { event ->
                when (event) {
                    AddCarEvent.InfoSaved -> navigator.openCarPhotos()
                    is AddCarEvent.Error -> showErrorDialog(event.message)
                    AddCarEvent.CarAdded -> Unit
                }
            }
        }
    }

    private fun saveInput(binding: FragmentAddCarInfoBinding) {
        viewModel.year = binding.yearInput.text.toString().trim()
        viewModel.brand = binding.brandInput.text.toString().trim()
        viewModel.model = binding.modelInput.text.toString().trim()
        viewModel.mileage = binding.mileageInput.text.toString().trim()
        viewModel.price = binding.priceInput.text.toString().trim()
        viewModel.description = binding.descriptionInput.text.toString().trim()
    }

    /** «Отправить» активна, только когда заполнены все поля и выбрана трансмиссия. */
    private fun updateButton(binding: FragmentAddCarInfoBinding) {
        binding.submitButton.isEnabled = viewModel.year.isNotBlank() &&
            viewModel.brand.isNotBlank() &&
            viewModel.model.isNotBlank() &&
            viewModel.transmission != null &&
            viewModel.mileage.isNotBlank() &&
            viewModel.price.isNotBlank() &&
            viewModel.description.isNotBlank()
    }

    /** Адаптер Spinner: подсказка (первый пункт) серая и недоступна для выбора. */
    private class HintSpinnerAdapter(context: Context, items: List<String>) :
        ArrayAdapter<String>(context, R.layout.item_spinner, items) {

        private val hintColor = ContextCompat.getColor(context, R.color.text_hint)
        private val textColor = ContextCompat.getColor(context, R.color.text_primary)

        override fun isEnabled(position: Int) = position != 0

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
            (super.getView(position, convertView, parent) as TextView).apply {
                setTextColor(if (position == 0) hintColor else textColor)
            }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
            (super.getDropDownView(position, convertView, parent) as TextView).apply {
                setTextColor(if (position == 0) hintColor else textColor)
            }
    }
}
