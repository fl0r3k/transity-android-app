package pl.transity.app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import pl.transity.app.fragments.FavoriteLinesFragment
import pl.transity.app.fragments.FavoriteStopsFragment

class FavoritesSectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val tabCount = 2

    override fun getCount() = tabCount

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> FavoriteStopsFragment()
            1 -> FavoriteLinesFragment()
            else -> null
        }
    }
}