package ru.mtuci.drivenext.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.ActivityMainBinding
import ru.mtuci.drivenext.domain.model.StartDestination
import ru.mtuci.drivenext.presentation.noconnection.NoConnectionFragment
import ru.mtuci.drivenext.presentation.onboarding.OnboardingFragment
import ru.mtuci.drivenext.presentation.splash.SplashFragment
import ru.mtuci.drivenext.presentation.stub.StubFragment

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

        if (savedInstanceState == null) show(SplashFragment())
    }

    override fun navigate(destination: StartDestination) {
        val fragment = when (destination) {
            StartDestination.NoConnection -> NoConnectionFragment()
            StartDestination.Onboarding -> OnboardingFragment()
            StartDestination.Welcome -> StubFragment.newInstance(R.string.stub_login)
            StartDestination.Home -> StubFragment.newInstance(R.string.stub_home)
        }
        show(fragment)
    }

    private fun show(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragmentContainer, fragment)
        }
    }
}
