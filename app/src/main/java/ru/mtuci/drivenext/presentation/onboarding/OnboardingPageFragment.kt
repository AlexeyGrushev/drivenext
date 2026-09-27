package ru.mtuci.drivenext.presentation.onboarding

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.FragmentOnboardingPageBinding

class OnboardingPageFragment : Fragment(R.layout.fragment_onboarding_page) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentOnboardingPageBinding.bind(view)
        val args = requireArguments()
        binding.image.setImageResource(args.getInt(ARG_IMAGE))
        (binding.image.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth =
            args.getFloat(ARG_IMAGE_WIDTH)
        binding.title.setText(args.getInt(ARG_TITLE))
        binding.description.setText(args.getInt(ARG_DESCRIPTION))
    }

    companion object {
        private const val ARG_IMAGE = "image"
        private const val ARG_IMAGE_WIDTH = "image_width"
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"

        fun newInstance(page: OnboardingPage) = OnboardingPageFragment().apply {
            arguments = bundleOf(
                ARG_IMAGE to page.imageRes,
                ARG_IMAGE_WIDTH to page.imageWidthPercent,
                ARG_TITLE to page.titleRes,
                ARG_DESCRIPTION to page.descriptionRes,
            )
        }
    }
}
