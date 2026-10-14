package ru.mtuci.drivenext.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.mtuci.drivenext.presentation.auth.LoginViewModel
import ru.mtuci.drivenext.presentation.booking.BookingDetailsViewModel
import ru.mtuci.drivenext.presentation.booking.BookingsViewModel
import ru.mtuci.drivenext.presentation.booking.CheckoutViewModel
import ru.mtuci.drivenext.presentation.cars.HomeViewModel
import ru.mtuci.drivenext.presentation.cars.SearchResultsViewModel
import ru.mtuci.drivenext.presentation.details.CarDetailsViewModel
import ru.mtuci.drivenext.presentation.favorites.FavoritesViewModel
import ru.mtuci.drivenext.presentation.noconnection.NoConnectionViewModel
import ru.mtuci.drivenext.presentation.onboarding.OnboardingViewModel
import ru.mtuci.drivenext.presentation.profile.ProfileViewModel
import ru.mtuci.drivenext.presentation.registration.RegistrationViewModel
import ru.mtuci.drivenext.presentation.settings.SettingsViewModel
import ru.mtuci.drivenext.presentation.splash.SplashViewModel

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        SplashViewModel::class.java -> SplashViewModel(container.resolveStartDestination)
        NoConnectionViewModel::class.java -> NoConnectionViewModel(container.resolveStartDestination)
        OnboardingViewModel::class.java -> OnboardingViewModel(container.onboardingRepository)
        LoginViewModel::class.java -> LoginViewModel(container.signIn, container.signInWithGoogle)
        RegistrationViewModel::class.java -> RegistrationViewModel(container.registerUser)
        HomeViewModel::class.java -> HomeViewModel(container.getCars)
        SearchResultsViewModel::class.java -> SearchResultsViewModel(container.searchCars)
        SettingsViewModel::class.java -> SettingsViewModel(container.getProfile)
        ProfileViewModel::class.java ->
            ProfileViewModel(container.getProfile, container.updateAvatar, container.signOut)
        CarDetailsViewModel::class.java ->
            CarDetailsViewModel(container.getCar, container.observeIsFavorite, container.toggleFavorite)
        CheckoutViewModel::class.java -> CheckoutViewModel(container.getCar, container.createBooking)
        BookingsViewModel::class.java -> BookingsViewModel(container.getBookings)
        BookingDetailsViewModel::class.java -> BookingDetailsViewModel(container.getBooking, container.cancelBooking)
        FavoritesViewModel::class.java -> FavoritesViewModel(container.observeFavorites)
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    } as T
}
