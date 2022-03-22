package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.FragmentDetailsBinding
import xyz.flussigkatz.spoonzilla.view.DishDetailsStateAdapter


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
        initStateAdapter()
    }

    private fun initStateAdapter() {
        arguments?.let { bundle ->
            dishDetailsStateAdapter = DishDetailsStateAdapter(this, bundle)
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

    companion object {
        private const val TAG = "DetailsFragment"
    }
}