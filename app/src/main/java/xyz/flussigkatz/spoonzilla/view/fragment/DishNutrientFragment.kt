package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.spoonzilla.databinding.FragmentDishNutrientBinding
import xyz.flussigkatz.spoonzilla.util.AppConst
import xyz.flussigkatz.spoonzilla.view.rv_adapter.NutrientRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.DishNutrientFragmentViewModel


class DishNutrientFragment : Fragment() {
    private lateinit var binding: FragmentDishNutrientBinding
    private lateinit var nutrientsAdapter: NutrientRecyclerAdapter
    private val nutrientsFragmentCoroutineScope = CoroutineScope(Dispatchers.IO)
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
        getNutrients()
    }

    private fun getNutrients() {
        arguments?.getParcelable<DishAdvancedInfo?>(AppConst.KEY_DISH)?.let { dishAdvancedInfo ->
            viewModel.getNutrientByIdFromDb(dishAdvancedInfo.id)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { initNutrientAdapter() }
                .subscribe(
                    { nutrientsAdapter.addItems(it) },
                    { Timber.d(it, "getNutrients onError") }
                )
            nutrientsFragmentCoroutineScope.launch {
                val ingredientsIsEmpty =
                    viewModel.getNutrientToListByIdFromDb(dishAdvancedInfo.id).isEmpty()
                if (ingredientsIsEmpty) viewModel.getNutrientByIdFromApi(dishAdvancedInfo.id)
                return@launch
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

    override fun onDestroy() {
        nutrientsFragmentCoroutineScope.cancel()
        super.onDestroy()
    }
}