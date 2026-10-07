package ru.mtuci.drivenext.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.FragmentWelcomeBinding
import ru.mtuci.drivenext.presentation.common.navigator

/** Экран «Вход / регистрация» (Getting started). */
class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentWelcomeBinding.bind(view)
        binding.loginButton.setOnClickListener { navigator.openLogin() }
        binding.registerButton.setOnClickListener { navigator.openSignUpStep1() }
    }
}
