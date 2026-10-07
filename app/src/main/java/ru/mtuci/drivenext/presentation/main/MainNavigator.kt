package ru.mtuci.drivenext.presentation.main

import androidx.annotation.StringRes

/** Навигация внутри главного контейнера с нижним меню. Реализует MainFragment. */
interface MainNavigator {
    fun openSearchResults(query: String)
    fun openProfile()
    fun openStub(@StringRes titleRes: Int)
    fun popBack()
}
