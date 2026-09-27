package ru.mtuci.drivenext

import android.app.Application
import androidx.fragment.app.Fragment
import ru.mtuci.drivenext.di.AppContainer
import ru.mtuci.drivenext.di.ViewModelFactory

class DriveNextApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

/** Фабрика ViewModel для фрагментов: by viewModels { appViewModelFactory() } */
fun Fragment.appViewModelFactory(): ViewModelFactory =
    ViewModelFactory((requireActivity().application as DriveNextApp).container)
