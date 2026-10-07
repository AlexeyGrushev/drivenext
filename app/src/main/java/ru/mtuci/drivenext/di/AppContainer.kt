package ru.mtuci.drivenext.di

import android.content.Context
import ru.mtuci.drivenext.BuildConfig
import ru.mtuci.drivenext.data.local.OnboardingRepositoryImpl
import ru.mtuci.drivenext.data.network.NetworkCheckerImpl
import ru.mtuci.drivenext.data.remote.ImageReader
import ru.mtuci.drivenext.data.remote.OAuthCallbackHandler
import ru.mtuci.drivenext.data.remote.SupabaseProvider
import ru.mtuci.drivenext.data.repository.AuthRepositoryImpl
import ru.mtuci.drivenext.data.repository.CarRepositoryImpl
import ru.mtuci.drivenext.data.repository.ProfileRepositoryImpl
import ru.mtuci.drivenext.data.repository.SessionRepositoryImpl
import ru.mtuci.drivenext.domain.repository.AuthRepository
import ru.mtuci.drivenext.domain.repository.CarRepository
import ru.mtuci.drivenext.domain.repository.NetworkChecker
import ru.mtuci.drivenext.domain.repository.OnboardingRepository
import ru.mtuci.drivenext.domain.repository.ProfileRepository
import ru.mtuci.drivenext.domain.repository.SessionRepository
import ru.mtuci.drivenext.domain.usecase.GetCarsUseCase
import ru.mtuci.drivenext.domain.usecase.GetProfileUseCase
import ru.mtuci.drivenext.domain.usecase.RegisterUserUseCase
import ru.mtuci.drivenext.domain.usecase.ResolveStartDestinationUseCase
import ru.mtuci.drivenext.domain.usecase.SearchCarsUseCase
import ru.mtuci.drivenext.domain.usecase.SignInUseCase
import ru.mtuci.drivenext.domain.usecase.SignInWithGoogleUseCase
import ru.mtuci.drivenext.domain.usecase.SignOutUseCase
import ru.mtuci.drivenext.domain.usecase.UpdateAvatarUseCase

/** Ручной DI-контейнер: создаёт зависимости один раз на всё приложение. */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    private val supabaseProvider = SupabaseProvider(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY)
    val oauthCallbackHandler = OAuthCallbackHandler(supabaseProvider)

    private val networkChecker: NetworkChecker = NetworkCheckerImpl(appContext)
    val onboardingRepository: OnboardingRepository = OnboardingRepositoryImpl(appContext)
    private val authRepository: AuthRepository = AuthRepositoryImpl(supabaseProvider)
    private val sessionRepository: SessionRepository = SessionRepositoryImpl(authRepository)
    private val profileRepository: ProfileRepository =
        ProfileRepositoryImpl(supabaseProvider, ImageReader(appContext))
    private val carRepository: CarRepository = CarRepositoryImpl(supabaseProvider)

    val resolveStartDestination =
        ResolveStartDestinationUseCase(networkChecker, onboardingRepository, sessionRepository)
    val signIn = SignInUseCase(authRepository)
    val signInWithGoogle = SignInWithGoogleUseCase(authRepository)
    val signOut = SignOutUseCase(authRepository)
    val registerUser = RegisterUserUseCase(authRepository, profileRepository)
    val getProfile = GetProfileUseCase(profileRepository)
    val updateAvatar = UpdateAvatarUseCase(profileRepository)
    val getCars = GetCarsUseCase(carRepository)
    val searchCars = SearchCarsUseCase(carRepository)
}
