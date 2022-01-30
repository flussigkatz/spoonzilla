package xyz.flussigkatz.spoonzilla

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

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