package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import timber.log.Timber
import xyz.flussigkatz.spoonzilla.databinding.FragmentDishInstructionsBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.view.rv_adapter.InstructionsItemRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.DishInstructionsFragmentViewModel

class DishInstructionsFragment : Fragment() {
    private lateinit var binding: FragmentDishInstructionsBinding
    private lateinit var instructionsAdapter: InstructionsItemRecyclerAdapter
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
        initInstructionsItemAdapter()
        getInstructions()
    }

    private fun getInstructions() {
        arguments?.let { bundle ->
            val dishId = bundle.getInt(KEY_DISH_ID)
            viewModel.getInstructionsByIdFromDb(dishId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { instructionsAdapter.addItems(it) },
                    { Timber.d(it, "getInstructions onError") }
                )
        }
    }

    private fun initInstructionsItemAdapter() {
        binding.instructionsItemRecycler.apply {
            instructionsAdapter = InstructionsItemRecyclerAdapter()
            adapter = instructionsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}