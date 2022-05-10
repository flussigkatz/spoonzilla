package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.FragmentDetailsBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.view.DishDetailsStateAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.DetailsFragmentViewModel


class DetailsFragment : Fragment() {
    private lateinit var dishDetailsStateAdapter: DishDetailsStateAdapter
    private lateinit var binding: FragmentDetailsBinding
    private val viewModel: DetailsFragmentViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.getParcelable<DishAdvancedInfo?>(KEY_DISH)?.let { initStateAdapter(it) }
            ?: arguments?.getInt(KEY_DISH_ID)?.let { getDishAdvancedInfo(it) }
    }

    private fun getDishAdvancedInfo(dishId: Int) {
        viewModel.getDishAdvancedInfoFromDb(dishId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.isNotEmpty()) initStateAdapter(it.first())
                    else viewModel.getRecipeByIdFromApi(dishId)
                },
                { Timber.d(it, "getDishAdvancedInfo onError") }
            )
    }

    private fun initStateAdapter(dishAdvancedInfo: DishAdvancedInfo) {
        dishDetailsStateAdapter = DishDetailsStateAdapter(this, dishAdvancedInfo)
        binding.recipeDetailsViewpager.adapter = dishDetailsStateAdapter
        initTabs()
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