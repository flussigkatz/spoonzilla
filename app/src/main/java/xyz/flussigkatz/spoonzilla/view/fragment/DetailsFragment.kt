package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.FragmentDetailsBinding
import xyz.flussigkatz.spoonzilla.util.AppConst
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.view.DishDetailsStateAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.DetailsFragmentViewModel


class DetailsFragment : Fragment() {
    private lateinit var dishDetailsStateAdapter: DishDetailsStateAdapter
    private lateinit var binding: FragmentDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dish = arguments?.getParcelable<DishAdvancedInfo>(KEY_DISH)
        val dishId = dish?.id
        dishId?.let {
            val bundle = Bundle().apply { putInt(KEY_DISH_ID, dishId) }
            initStateAdapter(bundle)
        } ?: initStateAdapter(arguments)
    }

    private fun initStateAdapter(bundle: Bundle?) {
        bundle?.let {
            dishDetailsStateAdapter = DishDetailsStateAdapter(this, it)
            binding.recipeDetailsViewpager.adapter = dishDetailsStateAdapter
            initTabs()
        }
    }

    private fun initTabs() {
        TabLayoutMediator(
            binding.recipeDetailsTabLayout,
            binding.recipeDetailsViewpager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = requireContext().getText(R.string.tab_name_overview)
                1 -> tab.text = requireContext().getText(R.string.tab_name_ingredients)
                2 -> tab.text = requireContext().getText(R.string.tab_name_equipments)
                3 -> tab.text = requireContext().getText(R.string.tab_name_instructions)
                4 -> tab.text = requireContext().getText(R.string.tab_name_nutrient)
            }
        }.attach()
    }
}