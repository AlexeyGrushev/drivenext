package ru.mtuci.drivenext.presentation.addcar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.FragmentCarAddedBinding
import ru.mtuci.drivenext.presentation.common.navigator

/** «Ваш автомобиль добавлен». */
class CarAddedFragment : Fragment(R.layout.fragment_car_added) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentCarAddedBinding.bind(view)
        binding.homeButton.setOnClickListener { navigator.openHome() }
    }
}
