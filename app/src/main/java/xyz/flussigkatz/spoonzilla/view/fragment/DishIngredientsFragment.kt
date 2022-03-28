package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import xyz.flussigkatz.core_api.entity.ingredients.IngredientItem
import xyz.flussigkatz.spoonzilla.databinding.FragmentDishIngredientsBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.view.rv_adapter.IngredientsRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.DishIngredientsFragmentViewModel

class DishIngredientsFragment : Fragment() {
    private lateinit var binding: FragmentDishIngredientsBinding
    private lateinit var ingredientsAdapter: IngredientsRecyclerAdapter
    private var ingredientItems: List<IngredientItem>? = null
    private val viewModel: DishIngredientsFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDishIngredientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initIngredientAdapter()
        getIngredients()
    }

    private fun getIngredients() {
        arguments?.let { bundle ->
            val dishId = bundle.getInt(KEY_DISH_ID)
            if (ingredientItems.isNullOrEmpty()) {
                viewModel.getIngredientsByIdFromDb(dishId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            ingredientItems = it
                            ingredientsAdapter.addItems(it)
                        },
                        { println("$TAG getIngredients onError: ${it.localizedMessage}") }
                    )
            } else {
                ingredientsAdapter.addItems(ingredientItems!!)
            }
        }
    }

    private fun initIngredientAdapter() {
        binding.ingredientsRecycler.apply {
            ingredientsAdapter = IngredientsRecyclerAdapter()
            adapter = ingredientsAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }

    companion object {
        private const val TAG = "DishIngredientsFragment"
    }

}