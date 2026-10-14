package ru.mtuci.drivenext.presentation.booking

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.Instant
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentBookingDetailsBinding
import ru.mtuci.drivenext.domain.model.BookingState
import ru.mtuci.drivenext.presentation.common.formatDateTime
import ru.mtuci.drivenext.presentation.common.formatDays
import ru.mtuci.drivenext.presentation.common.launchOnStarted
import ru.mtuci.drivenext.presentation.common.navigator
import ru.mtuci.drivenext.presentation.common.showErrorDialog

/** «Бронирование # …»: полная информация, стоимость и отмена активной брони. */
class BookingDetailsFragment : Fragment(R.layout.fragment_booking_details) {

    private val viewModel: BookingDetailsViewModel by viewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentBookingDetailsBinding.bind(view)
        viewModel.start(requireArguments().getLong(ARG_BOOKING_ID))

        binding.backButton.setOnClickListener { navigator.back() }
        binding.states.retryButton.setOnClickListener { viewModel.load() }
        binding.cancelButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.booking_cancel_confirm_title)
                .setMessage(R.string.booking_cancel_confirm_text)
                .setPositiveButton(R.string.booking_cancel_confirm_yes) { _, _ -> viewModel.cancel() }
                .setNegativeButton(R.string.booking_cancel_confirm_no, null)
                .show()
        }

        launchOnStarted {
            viewModel.state.collect { state ->
                binding.progress.isVisible = state.isLoading
                binding.states.errorContainer.isVisible = state.loadFailed && !state.isLoading
                binding.states.errorText.setText(R.string.error_load_failed)

                val booking = state.booking
                binding.content.isInvisible = booking == null
                if (booking != null) {
                    val bookingState = booking.stateAt(Instant.now())
                    binding.headerTitle.text = getString(R.string.booking_title_format, booking.id)
                    with(binding.carSummary) {
                        summaryModel.text = booking.model
                        summaryBrand.text = booking.brand
                        summaryPrice.text = getString(R.string.checkout_money_format, booking.pricePerDay)
                        summaryImage.load(booking.imageUrl) {
                            placeholder(R.drawable.car_placeholder)
                            error(R.drawable.car_placeholder)
                        }
                    }
                    binding.addressValue.text = booking.address ?: getString(R.string.booking_dash)
                    binding.startValue.text = resources.formatDateTime(booking.startAt)
                    binding.endValue.text = resources.formatDateTime(booking.endAt)
                    binding.driverValue.text = booking.driverName ?: getString(R.string.booking_dash)
                    binding.licenseValue.text = booking.licenseNumber ?: getString(R.string.booking_dash)
                    binding.statusValue.setText(
                        when (bookingState) {
                            BookingState.ACTIVE -> R.string.booking_status_approved
                            BookingState.COMPLETED -> R.string.booking_status_completed
                            BookingState.CANCELLED -> R.string.booking_status_cancelled
                        }
                    )
                    val days = resources.formatDays(booking.days)
                    binding.rentalLabel.text = "${getString(R.string.booking_rental)} $days:"
                    binding.insuranceLabel.text = "${getString(R.string.booking_insurance)} $days:"
                    binding.rentalValue.text = getString(R.string.checkout_price_per_day_format, booking.pricePerDay)
                    binding.insuranceValue.text =
                        getString(R.string.checkout_price_per_day_format, booking.insurancePerDay)
                    binding.totalValue.text = getString(R.string.checkout_money_format, booking.total)
                    // Отменить можно только активную аренду
                    binding.cancelButton.isInvisible = bookingState != BookingState.ACTIVE
                }
                binding.cancelButton.isLoading = state.isCancelling

                state.cancelError?.let { message ->
                    showErrorDialog(message)
                    viewModel.onErrorShown()
                }
                // Отмена выполнена: возвращаемся к списку бронирований
                if (state.cancelled) navigator.back()
            }
        }
    }

    companion object {
        private const val ARG_BOOKING_ID = "booking_id"

        fun newInstance(bookingId: Long) = BookingDetailsFragment().apply {
            arguments = bundleOf(ARG_BOOKING_ID to bookingId)
        }
    }
}
