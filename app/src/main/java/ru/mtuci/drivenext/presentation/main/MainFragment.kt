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
import ru.mtuci.drivenext.presentation.profile.ProfileFragment
import ru.mtuci.drivenext.presentation.settings.SettingsFragment
import ru.mtuci.drivenext.presentation.stub.StubFragment

/**
 * Контейнер главной части приложения с нижним меню из трёх вкладок.
 * Внутри своего childFragmentManager показывает экраны вкладок и вложенные экраны
 * (результаты поиска, профиль), у которых нижнее меню остаётся на месте.
 */
class MainFragment : Fragment(R.layout.fragment_main), MainNavigator {

    private enum class Tab(@IdRes val menuId: Int) {
        HOME(R.id.nav_home),
        BOOKINGS(R.id.nav_bookings),
        SETTINGS(R.id.nav_settings),
    }

    private var binding: FragmentMainBinding? = null
    private var currentTab = Tab.HOME

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentMainBinding.bind(view)
        binding = b

        savedInstanceState?.getString(KEY_TAB)?.let { currentTab = Tab.valueOf(it) }

        // Пункты меню описаны в res/menu/bottom_nav_menu.xml; id пункта -> вкладка
        b.bottomNav.setOnItemSelectedListener { item ->
            selectTab(
                when (item.itemId) {
                    R.id.nav_bookings -> Tab.BOOKINGS
                    R.id.nav_settings -> Tab.SETTINGS
                    else -> Tab.HOME
                }
            )
            true
        }
        // Повторное нажатие на уже выбранную вкладку возвращает на её корневой экран
        b.bottomNav.setOnItemReselectedListener { selectTab(currentTab) }

        if (savedInstanceState == null) {
            selectTab(Tab.HOME)
        } else {
            b.bottomNav.selectedItemId = currentTab.menuId
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TAB, currentTab.name)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun selectTab(tab: Tab) {
        currentTab = tab
        // Возврат на корень вкладки: вложенные экраны закрываются
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragment = when (tab) {
            Tab.HOME -> HomeFragment()
            Tab.BOOKINGS -> StubFragment.newInstance(R.string.stub_bookings)
            Tab.SETTINGS -> SettingsFragment()
        }
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.tabContainer, fragment)
        }
    }

    override fun openSearchResults(query: String) = push(SearchResultsFragment.newInstance(query))
    override fun openProfile() = push(ProfileFragment())
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
