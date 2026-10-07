package ru.mtuci.drivenext.presentation.registration

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.FragmentRegistrationSuccessBinding
import ru.mtuci.drivenext.presentation.common.navigator

class RegistrationSuccessFragment : Fragment(R.layout.fragment_registration_success) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentRegistrationSuccessBinding.bind(view)
        // На главный экран с очисткой стека навигации
        binding.nextButton.setOnClickListener { navigator.openHome() }
    }
}
