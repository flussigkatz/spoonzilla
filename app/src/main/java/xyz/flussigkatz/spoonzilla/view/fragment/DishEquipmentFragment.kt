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
import xyz.flussigkatz.core_api.entity.equipments.EquipmentItem
import xyz.flussigkatz.spoonzilla.databinding.FragmentDishEquipmentBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.view.rv_adapter.EquipmentRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.DishEquipmentsFragmentViewModel

class DishEquipmentFragment : Fragment() {
    private lateinit var binding: FragmentDishEquipmentBinding
    private lateinit var equipmentsAdapter: EquipmentRecyclerAdapter
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
        initEquipmentAdapter()
        getEquipments()
    }

    private fun getEquipments() {
        arguments?.let { bundle ->
            val dishId = bundle.getInt(KEY_DISH_ID)
            viewModel.getEquipmentsByIdFromDb(dishId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { equipmentsAdapter.addItems(it) },
                    { Timber.e(it, "getEquipments onError") }
                )
        }
    }

    private fun initEquipmentAdapter() {
        binding.equipmentsRecycler.apply {
            equipmentsAdapter = EquipmentRecyclerAdapter()
            adapter = equipmentsAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }
}