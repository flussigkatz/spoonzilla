package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.view.RecipeDetailsAdapter


class RecipeDetailsFragment : Fragment() {

    private lateinit var demoCollectionAdapter: RecipeDetailsAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        demoCollectionAdapter = RecipeDetailsAdapter(this)
        viewPager = view.findViewById(R.id.recipe_details_viewpager)
        viewPager.adapter = demoCollectionAdapter

        val tabLayout = view.findViewById<TabLayout>(R.id.recipe_details_tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = requireContext().getText(R.string.tab_name_overview)
                1 -> tab.text = requireContext().getText(R.string.tab_name_ingredients)
                2 -> tab.text = requireContext().getText(R.string.tab_name_instructions)
                3 -> tab.text = requireContext().getText(R.string.tab_name_nutrient)
            }
        }.attach()
    }

}