package ru.mtuci.drivenext.presentation.main

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.mtuci.drivenext.R

/** Временный главный экран (заменяется полноценным в следующем коммите). */
class MainFragment : Fragment(R.layout.fragment_stub) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.text).setText(R.string.stub_home)
    }
}
