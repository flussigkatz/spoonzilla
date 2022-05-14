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
import xyz.flussigkatz.spoonzilla.databinding.FragmentDishEquipmentBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH
import xyz.flussigkatz.spoonzilla.view.rv_adapter.EquipmentRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.DishEquipmentsFragmentViewModel

class DishEquipmentFragment : Fragment() {
    private lateinit var binding: FragmentDishEquipmentBinding
    private lateinit var equipmentsAdapter: EquipmentRecyclerAdapter
    private val equipmentsFragmentCoroutineScope = CoroutineScope(Dispatchers.IO)
    private val viewModel: DishEquipmentsFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDishEquipmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getEquipments()
    }

    private fun getEquipments() {
        arguments?.getParcelable<DishAdvancedInfo?>(KEY_DISH)?.let { dishAdvancedInfo ->
            viewModel.getEquipmentsByIdFromDb(dishAdvancedInfo.id)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { initEquipmentAdapter() }
                .subscribe(
                    { equipmentsAdapter.addItems(it) },
                    { Timber.d(it, "getEquipments onError") }
                )
            equipmentsFragmentCoroutineScope.launch {
                val ingredientsIsEmpty =
                    viewModel.getEquipmentsToListByIdFromDb(dishAdvancedInfo.id).isEmpty()
                if (ingredientsIsEmpty) viewModel.getEquipmentsByIdFromApi(dishAdvancedInfo.id)
                return@launch
            }
        }
    }

    private fun initEquipmentAdapter() {
        binding.equipmentsRecycler.apply {
            equipmentsAdapter = EquipmentRecyclerAdapter()
            adapter = equipmentsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroy() {
        equipmentsFragmentCoroutineScope.cancel()
        super.onDestroy()
    }
}