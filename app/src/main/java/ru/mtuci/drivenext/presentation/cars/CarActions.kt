package ru.mtuci.drivenext.presentation.cars

import androidx.fragment.app.Fragment
import ru.mtuci.drivenext.domain.model.Car
import ru.mtuci.drivenext.presentation.Navigator
import ru.mtuci.drivenext.presentation.main.MainNavigator

/** «Забронировать» → оформление аренды (поверх всего приложения, без нижнего меню). */
fun Fragment.openBooking(car: Car) = (requireActivity() as Navigator).openCheckout(car.id)

/** «Детали» → экран деталей внутри контейнера с нижним меню. */
fun Fragment.openCarDetails(car: Car) = (parentFragment as? MainNavigator)?.openCarDetails(car.id)
