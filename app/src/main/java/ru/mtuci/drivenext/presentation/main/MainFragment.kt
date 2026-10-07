package ru.mtuci.drivenext.presentation.main

import android.os.Bundle
import android.view.View
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

    private enum class Tab { HOME, BOOKINGS, SETTINGS }

    private var binding: FragmentMainBinding? = null
    private var currentTab = Tab.HOME

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = FragmentMainBinding.bind(view)
        binding = b

        savedInstanceState?.getString(KEY_TAB)?.let { currentTab = Tab.valueOf(it) }

        b.tabHome.setOnClickListener { selectTab(Tab.HOME) }
        b.tabBookings.setOnClickListener { selectTab(Tab.BOOKINGS) }
        b.tabSettings.setOnClickListener { selectTab(Tab.SETTINGS) }

        if (savedInstanceState == null) selectTab(Tab.HOME) else updateTabIcons()
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
        updateTabIcons()
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

    /** Выбранная вкладка — залитая иконка цвета primary; остальные — контурные чёрные. */
    private fun updateTabIcons() {
        val b = binding ?: return
        b.tabHomeIcon.setImageResource(
            if (currentTab == Tab.HOME) R.drawable.ic_nav_home_selected else R.drawable.ic_nav_home
        )
        b.tabBookingsIcon.setImageResource(
            if (currentTab == Tab.BOOKINGS) R.drawable.ic_nav_bookmark_selected else R.drawable.ic_nav_bookmark
        )
        b.tabSettingsIcon.setImageResource(
            if (currentTab == Tab.SETTINGS) R.drawable.ic_nav_settings_selected else R.drawable.ic_nav_settings
        )
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
