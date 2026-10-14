package ru.mtuci.drivenext.presentation.booking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.FragmentBookingSuccessBinding
import ru.mtuci.drivenext.presentation.common.navigator

/** «Удачной поездки»: бронирование создано. */
class BookingSuccessFragment : Fragment(R.layout.fragment_booking_success) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentBookingSuccessBinding.bind(view)
        binding.toBookingsButton.setOnClickListener { navigator.openBookingsAfterSuccess() }
        binding.homeButton.setOnClickListener { navigator.openHome() }
    }
}
