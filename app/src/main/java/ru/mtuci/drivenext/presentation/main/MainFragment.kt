package ru.mtuci.drivenext.presentation.main

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import ru.mtuci.drivenext.R
import ru.mtuci.drivenext.databinding.FragmentMainBinding
import ru.mtuci.drivenext.presentation.cars.HomeFragment
import ru.mtuci.drivenext.presentation.cars.SearchResultsFragment
import ru.mtuci.drivenext.presentation.details.CarDetailsFragment
import ru.mtuci.drivenext.presentation.favorites.FavoritesFragment
import ru.mtuci.drivenext.presentation.profile.ProfileFragment
import ru.mtuci.drivenext.presentation.settings.SettingsFragment
import ru.mtuci.drivenext.presentation.stub.StubFragment

/**
 * Контейнер главной части приложения с нижним меню (BottomNavigationView).
 * Внутри своего childFragmentManager показывает экраны вкладок и вложенные экраны
 * (результаты поиска, детали, профиль), у которых нижнее меню остаётся на месте.
 */
class MainFragment : Fragment(R.layout.fragment_main), MainNavigator {

    private enum class Tab(@IdRes val menuId: Int) {
        HOME(R.id.nav_home),
        FAVORITES(R.id.nav_favorites),
        SETTINGS(R.id.nav_settings),
    }

    private var binding: FragmentMainBinding? = null
    private var currentTab = Tab.HOME

    // Экземпляр фрагмента живёт, пока он в стеке (например, под экраном оформления аренды),
    // а его view пересоздаётся. Поэтому «первая загрузка» отслеживается полем, а не savedInstanceState.
    private var tabContentShown = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentMainBinding.bind(view)
        binding = b

        savedInstanceState?.getString(KEY_TAB)?.let { currentTab = Tab.valueOf(it) }

        // Показываем вкладку один раз; при возврате в экран содержимое уже восстановлено системой
        if (!tabContentShown) {
            tabContentShown = true
            showTab(currentTab)
        }
        // Выделение в меню ставим до подключения слушателя, чтобы не перезапускать экран вкладки
        b.bottomNav.selectedItemId = currentTab.menuId

        // Пункты меню описаны в res/menu/bottom_nav_menu.xml; id пункта -> вкладка
        b.bottomNav.setOnItemSelectedListener { item ->
            showTab(
                when (item.itemId) {
                    R.id.nav_favorites -> Tab.FAVORITES
                    R.id.nav_settings -> Tab.SETTINGS
                    else -> Tab.HOME
                }
            )
            true
        }
        // Повторное нажатие на уже выбранную вкладку возвращает на её корневой экран
        b.bottomNav.setOnItemReselectedListener { showTab(currentTab) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TAB, currentTab.name)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun showTab(tab: Tab) {
        currentTab = tab
        // Возврат на корень вкладки: вложенные экраны закрываются
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragment = when (tab) {
            Tab.HOME -> HomeFragment()
            Tab.FAVORITES -> FavoritesFragment()
            Tab.SETTINGS -> SettingsFragment()
        }
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.tabContainer, fragment)
        }
    }

    override fun openSearchResults(query: String) = push(SearchResultsFragment.newInstance(query))
    override fun openProfile() = push(ProfileFragment())
    override fun openCarDetails(carId: Long) = push(CarDetailsFragment.newInstance(carId))
    override fun openStub(titleRes: Int) = push(StubFragment.newInstance(titleRes))
    override fun popBack() {
        childFragmentManager.popBackStack()
    }

    private fun push(fragment: Fragment) {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.tabContainer, fragment)
            addToBackStack(null)
        }
    }

    private companion object {
        const val KEY_TAB = "tab"
    }
}
