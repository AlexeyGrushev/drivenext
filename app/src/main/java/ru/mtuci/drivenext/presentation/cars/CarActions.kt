package ru.mtuci.drivenext.presentation.cars

import androidx.fragment.app.Fragment
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.presentation.main.MainNavigator

/** «Забронировать» и «Детали» ведут на экраны следующих работ — пока это заглушки. */
fun Fragment.openBooking() = (parentFragment as? MainNavigator)?.openStub(R.string.stub_booking_checkout)

fun Fragment.openCarDetails() = (parentFragment as? MainNavigator)?.openStub(R.string.stub_car_details)
