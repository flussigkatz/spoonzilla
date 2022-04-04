package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import xyz.flussigkatz.core_api.entity.nutrient.NutrientItem
import xyz.flussigkatz.spoonzilla.databinding.FragmentDishNutrientBinding
import xyz.flussigkatz.spoonzilla.util.AppConst
import xyz.flussigkatz.spoonzilla.view.rv_adapter.NutrientRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.DishNutrientFragmentViewModel


class DishNutrientFragment : Fragment() {
    private lateinit var binding: FragmentDishNutrientBinding
    private lateinit var nutrientsAdapter: NutrientRecyclerAdapter
    private var nutrientItem: List<NutrientItem>? = null
    private val viewModel: DishNutrientFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDishNutrientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNutrientAdapter()
        getNutrients()
    }

    private fun getNutrients() {
        arguments?.let { bundle ->
            val dishId = bundle.getInt(AppConst.KEY_DISH_ID)
            if (nutrientItem.isNullOrEmpty()) {
                viewModel.getNutrientByIdFromDb(dishId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            nutrientItem = it
                            nutrientsAdapter.addItems(it)
                            println(it)
                        },
                        { println("$TAG getNutrients onError: ${it.localizedMessage}") }
                    )
            } else {
                nutrientsAdapter.addItems(nutrientItem!!)
            }
        }
    }

    private fun initNutrientAdapter() {
        binding.nutrientRecycler.apply {
            nutrientsAdapter = NutrientRecyclerAdapter()
            adapter = nutrientsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    companion object {
        private const val TAG = "DishNutrientFragment"
    }

}