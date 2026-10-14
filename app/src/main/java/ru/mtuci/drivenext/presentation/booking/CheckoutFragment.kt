package ru.mtuci.drivenext.presentation.booking

import android.graphics.Paint
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
import ru.mtuci.drivenext.databinding.FragmentCheckoutBinding
import ru.mtuci.drivenext.presentation.common.formatRentalDateTime
import ru.mtuci.drivenext.presentation.common.formatDays
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.common.navigator
import ru.mtuci.drivenext.presentation.common.showDateRangePicker
import ru.mtuci.drivenext.presentation.common.showErrorDialog

/** «Оформление аренды»: автомобиль, период (можно менять), адрес и расчёт стоимости. */
class CheckoutFragment : Fragment(R.layout.fragment_checkout) {

    private val viewModel: CheckoutViewModel by viewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentCheckoutBinding.bind(view)
        viewModel.start(requireArguments().getLong(ARG_CAR_ID))

        binding.backButton.setOnClickListener { navigator.back() }
        binding.states.retryButton.setOnClickListener { viewModel.load() }
        binding.depositLabel.paintFlags = binding.depositLabel.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.continueButton.setOnClickListener { viewModel.submit() }

        val pickPeriod = View.OnClickListener {
            val state = viewModel.state.value
            showDateRangePicker(state.start, state.end) { start, end -> viewModel.setPeriod(start, end) }
        }
        binding.startRow.setOnClickListener(pickPeriod)
        binding.endRow.setOnClickListener(pickPeriod)

        launchOnStarted {
            viewModel.state.collect { state ->
                binding.progress.isVisible = state.isLoading
                binding.states.errorContainer.isVisible = state.loadFailed && !state.isLoading
                binding.states.errorText.setText(R.string.error_load_failed)

                val car = state.car
                val quote = state.quote
                val showContent = car != null && quote != null
                binding.content.isInvisible = !showContent
                binding.continueButton.isEnabled = showContent
                binding.continueButton.isLoading = state.isSubmitting

                if (car != null && quote != null) {
                    with(binding.carSummary) {
                        summaryModel.text = car.model
                        summaryBrand.text = car.brand
                        summaryPrice.text = getString(R.string.checkout_money_format, car.pricePerDay)
                        summaryImage.load(car.mainImage) {
                            placeholder(R.drawable.car_placeholder)
                            error(R.drawable.car_placeholder)
                        }
                    }
                    binding.startValue.text = resources.formatRentalDateTime(state.start)
                    binding.endValue.text = resources.formatRentalDateTime(state.end)
                    binding.addressValue.text = car.address ?: getString(R.string.details_no_address)

                    val days = resources.formatDays(quote.days)
                    binding.rentalLabel.text = "${getString(R.string.checkout_rental)} $days:"
                    binding.insuranceLabel.text = "${getString(R.string.checkout_insurance)} $days:"
                    binding.rentalValue.text = getString(R.string.checkout_price_per_day_format, quote.pricePerDay)
                    binding.insuranceValue.text = getString(R.string.checkout_price_per_day_format, quote.insurancePerDay)
                    binding.totalValue.text = getString(R.string.checkout_money_format, quote.total)
                    binding.depositValue.text = getString(R.string.checkout_money_format, quote.deposit)
                }

                state.submitError?.let { message ->
                    showErrorDialog(message)
                    viewModel.onErrorShown()
                }
                // Успех: следующий экран, стек навигации очищается
                if (state.booked) navigator.openBookingSuccess()
            }
        }
    }

    companion object {
        private const val ARG_CAR_ID = "car_id"

        fun newInstance(carId: Long) = CheckoutFragment().apply {
            arguments = bundleOf(ARG_CAR_ID to carId)
        }
    }
}
