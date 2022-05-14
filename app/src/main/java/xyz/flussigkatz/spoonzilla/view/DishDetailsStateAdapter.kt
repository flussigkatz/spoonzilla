package xyz.flussigkatz.spoonzilla.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.spoonzilla.util.AppConst
import xyz.flussigkatz.spoonzilla.view.fragment.*

class DishDetailsStateAdapter(
    parentFragment: Fragment,
    private val dishAdvancedInfo: DishAdvancedInfo
) :
    FragmentStateAdapter(parentFragment) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle().apply { putParcelable(AppConst.KEY_DISH, dishAdvancedInfo) }
        return when (position) {
            0 -> DishOverviewFragment().apply { arguments = bundle }
            1 -> DishIngredientsFragment().apply { arguments = bundle }
            2 -> DishEquipmentFragment().apply { arguments = bundle }
            3 -> DishInstructionsFragment().apply { arguments = bundle }
            4 -> DishNutrientFragment().apply { arguments = bundle }
            else -> throw IllegalArgumentException("Wrong position")
        }
    }

}