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
import xyz.flussigkatz.spoonzilla.databinding.FragmentDishInstructionsBinding
import xyz.flussigkatz.spoonzilla.util.AppConst
import xyz.flussigkatz.spoonzilla.view.rv_adapter.InstructionsItemRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.DishInstructionsFragmentViewModel

class DishInstructionsFragment : Fragment() {
    private lateinit var binding: FragmentDishInstructionsBinding
    private lateinit var instructionsAdapter: InstructionsItemRecyclerAdapter
    private val instructionsFragmentCoroutineScope = CoroutineScope(Dispatchers.IO)
    private val viewModel: DishInstructionsFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDishInstructionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInstructions()
    }

    private fun getInstructions() {
        arguments?.getParcelable<DishAdvancedInfo?>(AppConst.KEY_DISH)?.let { dishAdvancedInfo ->
            viewModel.getInstructionsByIdFromDb(dishAdvancedInfo.id)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { initInstructionsItemAdapter() }
                .subscribe(
                    { instructionsAdapter.addItems(it) },
                    { Timber.d(it, "getInstructions onError") }
                )
            instructionsFragmentCoroutineScope.launch {
                val ingredientsIsEmpty =
                    viewModel.getInstructionsToListByIdFromDb(dishAdvancedInfo.id).isEmpty()
                if (ingredientsIsEmpty) viewModel.getInstructionsByIdFromApi(dishAdvancedInfo.id)
                return@launch
            }
        }
    }

    private fun initInstructionsItemAdapter() {
        binding.instructionsItemRecycler.apply {
            instructionsAdapter = InstructionsItemRecyclerAdapter()
            adapter = instructionsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroy() {
        instructionsFragmentCoroutineScope.cancel()
        super.onDestroy()
    }
}