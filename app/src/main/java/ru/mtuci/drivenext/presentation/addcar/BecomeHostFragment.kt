package ru.mtuci.drivenext.presentation.addcar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.appViewModelFactory
import ru.mtuci.drivenext.databinding.FragmentBecomeHostBinding
import ru.mtuci.drivenext.presentation.common.navigator

/** «Стать арендодателем»: информационные блоки и кнопка «Начать». */
class BecomeHostFragment : Fragment(R.layout.fragment_become_host) {

    private val viewModel: AddCarViewModel by activityViewModels { appViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentBecomeHostBinding.bind(view)
        binding.backButton.setOnClickListener { navigator.back() }
        binding.startButton.setOnClickListener {
            viewModel.reset() // каждое новое добавление начинается с пустых полей
            navigator.openAddCarAddress()
        }
    }
}
