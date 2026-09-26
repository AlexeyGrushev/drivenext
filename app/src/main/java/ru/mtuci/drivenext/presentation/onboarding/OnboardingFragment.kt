package ru.mtuci.drivenext.presentation.onboarding

import android.os.Bundle
import android.view.View
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentOnboardingBinding
import ru.mtuci.drivenext.domain.model.StartDestination
import ru.mtuci.drivenext.presentation.Navigator

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val viewModel: OnboardingViewModel by viewModels { appViewModelFactory() }

    private val pages = listOf(
        OnboardingPage(R.drawable.onboarding_1, R.string.onboarding_1_title, R.string.onboarding_1_text),
        OnboardingPage(R.drawable.onboarding_2, R.string.onboarding_2_title, R.string.onboarding_2_text, 341f / 390f),
        OnboardingPage(R.drawable.onboarding_3, R.string.onboarding_3_title, R.string.onboarding_3_text),
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentOnboardingBinding.bind(view)
        val dots = listOf(binding.dot1, binding.dot2, binding.dot3)

        binding.viewPager.adapter = OnboardingPagerAdapter(this, pages)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Кнопка: «Далее» на слайдах 1–2, «Поехали» на последнем
                binding.nextButton.setText(
                    if (position == pages.lastIndex) R.string.onboarding_go else R.string.onboarding_next
                )
                // Индикатор: активная точка длинная и тёмная
                dots.forEachIndexed { index, dot ->
                    val active = index == position
                    dot.setBackgroundResource(
                        if (active) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
                    )
                    dot.updateLayoutParams {
                        width = resources.getDimensionPixelSize(
                            if (active) R.dimen.dot_width_active else R.dimen.dot_width_inactive
                        )
                    }
                }
            }
        })

        binding.nextButton.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < pages.lastIndex) {
                binding.viewPager.setCurrentItem(current + 1, true)
            } else {
                finishOnboarding()
            }
        }
        binding.skipButton.setOnClickListener { finishOnboarding() }
    }

    private fun finishOnboarding() {
        viewModel.complete() // запоминаем: повторно не показывать
        (requireActivity() as Navigator).navigate(StartDestination.Login)
    }
}
