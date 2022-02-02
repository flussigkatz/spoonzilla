package xyz.flussigkatz.spoonzilla.view

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.flussigkatz.spoonzilla.view.fragment.RecipeIngredientsFragment
import xyz.flussigkatz.spoonzilla.view.fragment.RecipeInstructionsFragment
import xyz.flussigkatz.spoonzilla.view.fragment.RecipeNutrientFragment
import xyz.flussigkatz.spoonzilla.view.fragment.RecipeOverviewFragment

class RecipeDetailsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RecipeOverviewFragment()
            1 -> RecipeIngredientsFragment()
            2 -> RecipeInstructionsFragment()
            3 -> RecipeNutrientFragment()
            else -> RecipeOverviewFragment()
        }
    }
}