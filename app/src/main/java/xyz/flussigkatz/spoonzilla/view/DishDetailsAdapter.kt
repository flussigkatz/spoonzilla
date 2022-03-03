package xyz.flussigkatz.spoonzilla.view

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.flussigkatz.spoonzilla.view.fragment.DishIngredientsFragment
import xyz.flussigkatz.spoonzilla.view.fragment.DishInstructionsFragment
import xyz.flussigkatz.spoonzilla.view.fragment.DishNutrientFragment
import xyz.flussigkatz.spoonzilla.view.fragment.DishOverviewFragment

class DishDetailsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DishOverviewFragment()
            1 -> DishIngredientsFragment()
            2 -> DishInstructionsFragment()
            3 -> DishNutrientFragment()
            else -> DishOverviewFragment()
        }
    }
}