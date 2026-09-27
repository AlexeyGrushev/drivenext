package ru.mtuci.drivenext.presentation.stub

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import ru.mtuci.drivenext.R

/** Заглушка для экранов, которых нет в ЛР1 (вход, главный). */
class StubFragment : Fragment(R.layout.fragment_stub) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.text).setText(requireArguments().getInt(ARG_TEXT))
    }

    companion object {
        private const val ARG_TEXT = "text"

        fun newInstance(@StringRes textRes: Int) =
            StubFragment().apply { arguments = bundleOf(ARG_TEXT to textRes) }
    }
}
