package ru.mtuci.drivenext.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import ru.mtuci.drivenext.DriveNextApp
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.ActivityMainBinding
import ru.mtuci.drivenext.domain.model.StartDestination
import ru.mtuci.drivenext.presentation.auth.LoginFragment
import ru.mtuci.drivenext.presentation.auth.WelcomeFragment
import ru.mtuci.drivenext.presentation.main.MainFragment
import ru.mtuci.drivenext.presentation.noconnection.NoConnectionFragment
import ru.mtuci.drivenext.presentation.onboarding.OnboardingFragment
import ru.mtuci.drivenext.presentation.registration.RegistrationSuccessFragment
import ru.mtuci.drivenext.presentation.registration.SignUpStep1Fragment
import ru.mtuci.drivenext.presentation.registration.SignUpStep2Fragment
import ru.mtuci.drivenext.presentation.registration.SignUpStep3Fragment
import ru.mtuci.drivenext.presentation.splash.SplashFragment

class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // ВАЖНО: до super.onCreate() и setContentView() — иначе SplashScreen не свяжется с жизненным циклом
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Отступы под статус-бар и навигационную панель
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            replaceRoot(SplashFragment())
            handleOAuthCallback(intent)
        }
    }

    // launchMode=singleTop: возврат из браузера после входа через Google приходит сюда
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOAuthCallback(intent)
    }

    private fun handleOAuthCallback(intent: Intent?) {
        val container = (application as DriveNextApp).container
        container.oauthCallbackHandler.handle(intent) { openHome() }
    }

    override fun navigate(destination: StartDestination) {
        when (destination) {
            StartDestination.NoConnection -> replaceRoot(NoConnectionFragment())
            StartDestination.Onboarding -> replaceRoot(OnboardingFragment())
            StartDestination.Welcome -> replaceRoot(WelcomeFragment())
            StartDestination.Home -> openHome()
        }
    }

    override fun openLogin() = push(LoginFragment())
    override fun openSignUpStep1() = push(SignUpStep1Fragment())
    override fun openSignUpStep2() = push(SignUpStep2Fragment())
    override fun openSignUpStep3() = push(SignUpStep3Fragment())
    override fun openRegistrationSuccess() = replaceRoot(RegistrationSuccessFragment())
    override fun openHome() = replaceRoot(MainFragment())
    override fun openLoginAsRoot() = replaceRoot(LoginFragment())
    override fun back() = onBackPressedDispatcher.onBackPressed()

    /** Заменяет экран и очищает стек навигации. */
    private fun replaceRoot(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragmentContainer, fragment)
        }
    }

    /** Открывает экран поверх текущего: кнопка «Назад» вернёт на предыдущий. */
    private fun push(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragmentContainer, fragment)
            addToBackStack(null)
        }
    }
}
